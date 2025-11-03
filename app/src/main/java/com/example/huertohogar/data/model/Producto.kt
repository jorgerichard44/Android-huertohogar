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
    val categoria: String, // "Verduras", "Frutas", "Hierbas"
    val stock: Int,
    val imagen: String = "", // URL o nombre de recurso
    val esOrganico: Boolean = true
)
