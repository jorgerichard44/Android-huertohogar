package com.example.huertohogar.data.model



data class Producto(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0,
    val categoria: String = "",
    val origen: String = "",
    val disponible: Boolean = true,
    val imagenUrl: String = "",
    val stock: Int = 0
)
