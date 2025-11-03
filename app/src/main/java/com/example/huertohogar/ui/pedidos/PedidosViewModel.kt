package com.example.huertohogar.ui.pedidos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.huertohogar.data.model.DetallePedido
import com.example.huertohogar.data.model.Pedido
import com.example.huertohogar.data.repository.PedidoRepository
import com.example.huertohogar.ui.common.UIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PedidosViewModel(
    private val pedidoRepository: PedidoRepository
) : ViewModel() {

    private val _pedidos = MutableStateFlow<List<Pedido>>(emptyList())
    val pedidos: StateFlow<List<Pedido>> = _pedidos

    private val _detallesPedido = MutableStateFlow<List<DetallePedido>>(emptyList())
    val detallesPedido: StateFlow<List<DetallePedido>> = _detallesPedido

    private val _pedidoSeleccionado = MutableStateFlow<Pedido?>(null)
    val pedidoSeleccionado: StateFlow<Pedido?> = _pedidoSeleccionado

    private val _cancelarState = MutableStateFlow<UIState<Unit>>(UIState.Idle)
    val cancelarState: StateFlow<UIState<Unit>> = _cancelarState

    fun cargarPedidos(usuarioId: Int) {
        viewModelScope.launch {
            pedidoRepository.obtenerPedidosPorUsuario(usuarioId).collectLatest { listaPedidos ->
                _pedidos.value = listaPedidos
            }
        }
    }

    fun cargarDetallesPedido(pedidoId: Int) {
        viewModelScope.launch {
            pedidoRepository.obtenerPedidoPorIdFlow(pedidoId).collectLatest { pedido ->
                _pedidoSeleccionado.value = pedido
            }

            pedidoRepository.obtenerDetallesPorPedido(pedidoId).collectLatest { detalles ->
                _detallesPedido.value = detalles
            }
        }
    }

    fun cancelarPedido(pedidoId: Int) {
        viewModelScope.launch {
            _cancelarState.value = UIState.Loading
            try {
                pedidoRepository.cancelarPedido(pedidoId)
                _cancelarState.value = UIState.Success(Unit)
            } catch (e: Exception) {
                _cancelarState.value = UIState.Error(e.message ?: "Error al cancelar pedido")
            }
        }
    }

    fun resetState() {
        _cancelarState.value = UIState.Idle
    }
}

class PedidosViewModelFactory(
    private val pedidoRepository: PedidoRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PedidosViewModel::class.java)) {
            return PedidosViewModel(pedidoRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
