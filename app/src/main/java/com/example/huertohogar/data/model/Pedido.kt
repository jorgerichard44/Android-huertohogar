package com.example.huertohogar.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "pedidos",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Pedido(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val usuarioId: Int,
    val fechaPedido: Long = System.currentTimeMillis(),
    val total: Double,
    val estado: String, // "Pendiente", "En proceso", "Enviado", "Entregado", "Cancelado"
    val direccionEntrega: String,
    val metodoPago: String // "Tarjeta", "Transferencia", "Efectivo"
)
