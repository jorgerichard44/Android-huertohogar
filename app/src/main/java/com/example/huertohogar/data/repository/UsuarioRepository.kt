package com.example.huertohogar.data.repository

import com.example.huertohogar.data.dao.UsuarioDao
import com.example.huertohogar.data.model.Usuario
import kotlinx.coroutines.flow.Flow

class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    suspend fun login(email: String, password: String): Usuario? {
        return usuarioDao.login(email, password)
    }

    suspend fun registrar(usuario: Usuario): Result<Long> {
        return try {
            // Verificar si el email ya existe
            val usuarioExistente = usuarioDao.obtenerUsuarioPorEmail(usuario.email)
            if (usuarioExistente != null) {
                Result.failure(Exception("El email ya está registrado"))
            } else {
                val id = usuarioDao.insertarUsuario(usuario)
                Result.success(id)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerUsuarioPorId(id: Int): Usuario? {
        return usuarioDao.obtenerUsuarioPorId(id)
    }

    fun obtenerUsuarioPorIdFlow(id: Int): Flow<Usuario?> {
        return usuarioDao.obtenerUsuarioPorIdFlow(id)
    }

    suspend fun actualizarUsuario(usuario: Usuario) {
        usuarioDao.actualizarUsuario(usuario)
    }

    suspend fun eliminarUsuario(usuario: Usuario) {
        usuarioDao.eliminarUsuario(usuario)
    }
}
