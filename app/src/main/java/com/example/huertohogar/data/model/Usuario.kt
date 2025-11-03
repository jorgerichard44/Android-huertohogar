package com.example.huertohogar.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val email: String,
    val password: String,
    val telefono: String = "",
    val direccion: String = "",
    val esAdmin: Boolean = false
)
