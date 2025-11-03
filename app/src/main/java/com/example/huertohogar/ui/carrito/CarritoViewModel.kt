package com.example.huertohogar.ui.carrito


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huertohogar.data.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ItemCarrito(
    val producto: Producto,
    val cantidad: Int
)

class CarritoViewModel : ViewModel() {

    private val _items = MutableStateFlow<List<ItemCarrito>>(emptyList())
    val items: StateFlow<List<ItemCarrito>> = _items.asStateFlow()

    private val _total = MutableStateFlow(0.0)
    val total: StateFlow<Double> = _total.asStateFlow()

    fun agregarProducto(producto: Producto, cantidad: Int = 1) {
        viewModelScope.launch {
            val itemsActuales = _items.value.toMutableList()
            val itemExistente = itemsActuales.find { it.producto.id == producto.id }

            if (itemExistente != null) {
                val index = itemsActuales.indexOf(itemExistente)
                itemsActuales[index] = itemExistente.copy(
                    cantidad = itemExistente.cantidad + cantidad
                )
            } else {
                itemsActuales.add(ItemCarrito(producto, cantidad))
            }

            _items.value = itemsActuales
            calcularTotal()
        }
    }

    fun actualizarCantidad(productoId: Int, nuevaCantidad: Int) {
        viewModelScope.launch {
            val itemsActuales = _items.value.toMutableList()
            val item = itemsActuales.find { it.producto.id == productoId }

            item?.let {
                val index = itemsActuales.indexOf(it)
                if (nuevaCantidad > 0) {
                    itemsActuales[index] = it.copy(cantidad = nuevaCantidad)
                } else {
                    itemsActuales.removeAt(index)
                }
            }

            _items.value = itemsActuales
            calcularTotal()
        }
    }

    fun eliminarProducto(productoId: Int) {
        viewModelScope.launch {
            _items.value = _items.value.filter { it.producto.id != productoId }
            calcularTotal()
        }
    }

    fun vaciarCarrito() {
        _items.value = emptyList()
        _total.value = 0.0
    }

    private fun calcularTotal() {
        _total.value = _items.value.sumOf { it.producto.precio * it.cantidad }
    }
}
