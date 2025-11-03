package com.example.huertohogar.ui.carrito

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.huertohogar.data.model.ItemCarrito
import com.example.huertohogar.data.model.Producto
import com.example.huertohogar.data.repository.PedidoRepository
import com.example.huertohogar.ui.common.UIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CarritoViewModel(
    private val pedidoRepository: PedidoRepository
) : ViewModel() {

    private val _carrito = MutableStateFlow<List<ItemCarrito>>(emptyList())
    val carrito: StateFlow<List<ItemCarrito>> = _carrito

    private val _total = MutableStateFlow(0.0)
    val total: StateFlow<Double> = _total

    private val _pedidoState = MutableStateFlow<UIState<Long>>(UIState.Idle)
    val pedidoState: StateFlow<UIState<Long>> = _pedidoState

    fun agregarProducto(producto: Producto) {
        val carritoActual = _carrito.value.toMutableList()
        val itemExistente = carritoActual.find { it.producto.id == producto.id }

        if (itemExistente != null) {
            itemExistente.cantidad++
        } else {
            carritoActual.add(ItemCarrito(producto, 1))
        }

        _carrito.value = carritoActual
        calcularTotal()
    }

    fun eliminarProducto(productoId: Int) {
        _carrito.value = _carrito.value.filter { it.producto.id != productoId }
        calcularTotal()
    }

    fun actualizarCantidad(productoId: Int, cantidad: Int) {
        val carritoActual = _carrito.value.toMutableList()
        val item = carritoActual.find { it.producto.id == productoId }

        if (item != null) {
            if (cantidad > 0) {
                item.cantidad = cantidad
            } else {
                carritoActual.remove(item)
            }
        }

        _carrito.value = carritoActual
        calcularTotal()
    }

    private fun calcularTotal() {
        _total.value = _carrito.value.sumOf { it.subtotal }
    }

    fun crearPedido(
        usuarioId: Int,
        direccionEntrega: String,
        metodoPago: String
    ) {
        viewModelScope.launch {
            _pedidoState.value = UIState.Loading

            try {
                val result = pedidoRepository.crearPedido(
                    usuarioId = usuarioId,
                    carrito = _carrito.value,
                    direccionEntrega = direccionEntrega,
                    metodoPago = metodoPago
                )

                result.onSuccess { pedidoId ->
                    _carrito.value = emptyList()
                    _total.value = 0.0
                    _pedidoState.value = UIState.Success(pedidoId)
                }.onFailure { error ->
                    _pedidoState.value = UIState.Error(error.message ?: "Error al crear pedido")
                }

            } catch (e: Exception) {
                _pedidoState.value = UIState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun limpiarCarrito() {
        _carrito.value = emptyList()
        _total.value = 0.0
    }

    fun resetState() {
        _pedidoState.value = UIState.Idle
    }
}

class CarritoViewModelFactory(
    private val pedidoRepository: PedidoRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CarritoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CarritoViewModel(pedidoRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
