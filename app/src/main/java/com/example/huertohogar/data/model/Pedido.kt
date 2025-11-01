package com.example.huertohogar.data.model



data class Pedido(
    val id: String = "",
    val usuarioId: String = "",
    val productos: List<ItemPedido> = emptyList(),
    val total: Double = 0.0,
    val estado: EstadoPedido = EstadoPedido.PENDIENTE,
    val fechaCreacion: Long = System.currentTimeMillis(),
    val direccionEntrega: String = ""
)

data class ItemPedido(
    val productoId: String = "",
    val nombre: String = "",
    val cantidad: Int = 0,
    val precioUnitario: Double = 0.0
)

enum class EstadoPedido {
    PENDIENTE,
    EN_PREPARACION,
    EN_CAMINO,
    ENTREGADO,
    CANCELADO
}
