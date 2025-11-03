package com.example.huertohogar.data.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "detalle_pedidos",
    foreignKeys = [
        ForeignKey(
            entity = Pedido::class,
            parentColumns = ["id"],
            childColumns = ["pedidoId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Producto::class,
            parentColumns = ["id"],
            childColumns = ["productoId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DetallePedido(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val pedidoId: Int,
    val productoId: Int,
    val nombreProducto: String,
    val cantidad: Int,
    val precioUnitario: Double,
    val subtotal: Double
)
