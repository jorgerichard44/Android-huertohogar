package com.example.huertohogar.data.repository


import com.example.huertohogar.data.dao.PedidoDao
import com.example.huertohogar.data.dao.DetallePedidoDao
import com.example.huertohogar.data.dao.ProductoDao
import com.example.huertohogar.data.model.Pedido
import com.example.huertohogar.data.model.DetallePedido
import com.example.huertohogar.data.model.ItemCarrito
import kotlinx.coroutines.flow.Flow

class PedidoRepository(
    private val pedidoDao: PedidoDao,
    private val detallePedidoDao: DetallePedidoDao,
    private val productoDao: ProductoDao
) {

    fun obtenerPedidosPorUsuario(usuarioId: Int): Flow<List<Pedido>> {
        return pedidoDao.obtenerPedidosPorUsuario(usuarioId)
    }

    suspend fun obtenerPedidoPorId(pedidoId: Int): Pedido? {
        return pedidoDao.obtenerPedidoPorId(pedidoId)
    }

    fun obtenerPedidoPorIdFlow(pedidoId: Int): Flow<Pedido?> {
        return pedidoDao.obtenerPedidoPorIdFlow(pedidoId)
    }

    fun obtenerDetallesPorPedido(pedidoId: Int): Flow<List<DetallePedido>> {
        return detallePedidoDao.obtenerDetallesPorPedido(pedidoId)
    }

    suspend fun crearPedido(
        usuarioId: Int,
        carrito: List<ItemCarrito>,
        direccionEntrega: String,
        metodoPago: String
    ): Result<Long> {
        return try {
            val total = carrito.sumOf { it.subtotal }

            val pedido = Pedido(
                usuarioId = usuarioId,
                total = total,
                estado = "Pendiente",
                direccionEntrega = direccionEntrega,
                metodoPago = metodoPago
            )

            val pedidoId = pedidoDao.insertarPedido(pedido)

            val detalles = carrito.map { item ->
                DetallePedido(
                    pedidoId = pedidoId.toInt(),
                    productoId = item.producto.id,
                    nombreProducto = item.producto.nombre,
                    cantidad = item.cantidad,
                    precioUnitario = item.producto.precio,
                    subtotal = item.subtotal
                )
            }

            detallePedidoDao.insertarDetalles(detalles)

            // Reducir stock
            carrito.forEach { item ->
                productoDao.reducirStock(item.producto.id, item.cantidad)
            }

            Result.success(pedidoId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarEstado(pedidoId: Int, nuevoEstado: String) {
        pedidoDao.actualizarEstado(pedidoId, nuevoEstado)
    }

    suspend fun cancelarPedido(pedidoId: Int) {
        pedidoDao.actualizarEstado(pedidoId, "Cancelado")
    }
}
