package com.example.huertohogar.data.dao


import androidx.room.*
import com.example.huertohogar.data.model.DetallePedido
import kotlinx.coroutines.flow.Flow

@Dao
interface DetallePedidoDao {

    @Query("SELECT * FROM detalle_pedidos WHERE pedidoId = :pedidoId")
    fun obtenerDetallesPorPedido(pedidoId: Int): Flow<List<DetallePedido>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarDetalle(detalle: DetallePedido)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarDetalles(detalles: List<DetallePedido>)

    @Delete
    suspend fun eliminarDetalle(detalle: DetallePedido)
}
