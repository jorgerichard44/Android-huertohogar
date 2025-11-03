package com.example.huertohogar.data.dao

import androidx.room.*
import com.example.huertohogar.data.model.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuarios WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun obtenerUsuarioPorEmail(email: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun obtenerUsuarioPorId(id: Int): Usuario?

    @Query("SELECT * FROM usuarios WHERE id = :id")
    fun obtenerUsuarioPorIdFlow(id: Int): Flow<Usuario?>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertarUsuario(usuario: Usuario): Long

    @Update
    suspend fun actualizarUsuario(usuario: Usuario)

    @Delete
    suspend fun eliminarUsuario(usuario: Usuario)
}
