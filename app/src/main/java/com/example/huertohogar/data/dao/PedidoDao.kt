package com.example.huertohogar.data.dao

import androidx.room.*
import com.example.huertohogar.data.model.Pedido
import kotlinx.coroutines.flow.Flow

@Dao
interface PedidoDao {

    @Query("SELECT * FROM pedidos WHERE usuarioId = :usuarioId ORDER BY fechaPedido DESC")
    fun obtenerPedidosPorUsuario(usuarioId: Int): Flow<List<Pedido>>

    @Query("SELECT * FROM pedidos WHERE id = :pedidoId")
    suspend fun obtenerPedidoPorId(pedidoId: Int): Pedido?

    @Query("SELECT * FROM pedidos WHERE id = :pedidoId")
    fun obtenerPedidoPorIdFlow(pedidoId: Int): Flow<Pedido?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPedido(pedido: Pedido): Long

    @Update
    suspend fun actualizarPedido(pedido: Pedido)

    @Delete
    suspend fun eliminarPedido(pedido: Pedido)

    @Query("UPDATE pedidos SET estado = :nuevoEstado WHERE id = :pedidoId")
    suspend fun actualizarEstado(pedidoId: Int, nuevoEstado: String)
}
