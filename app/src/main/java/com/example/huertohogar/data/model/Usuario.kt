package com.example.huertohogar.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["email"], unique = true)]
)
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nombre: String,
    val apellido: String,
    val email: String,
    val password: String,
    val telefono: String,
    val direccion: String,
    val ciudad: String,
    val region: String,
    val fechaRegistro: Long = System.currentTimeMillis()
)
