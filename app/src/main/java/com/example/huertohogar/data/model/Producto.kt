package com.example.huertohogar.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val stock: Int,
    val categoria: String, // Ej: "Frutas", "Verduras", "Hortalizas"
    val imagen: String? = null, // URL o nombre del recurso
    val unidad: String = "kg", // kg, unidad, etc.
    val disponible: Boolean = true
)
