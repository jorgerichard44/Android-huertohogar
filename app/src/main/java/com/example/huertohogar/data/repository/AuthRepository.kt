package com.example.huertohogar.data.repository



import com.example.huertohogar.data.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthRepository {
    private val _currentUser = MutableStateFlow<Usuario?>(null)
    val currentUser: StateFlow<Usuario?> = _currentUser

    // Simulación de base de datos en memoria
    private val usuarios = mutableListOf(
        Usuario(
            id = "1",
            nombre = "Usuario Demo",
            email = "demo@huertohogar.cl",
            password = "123456",
            telefono = "+56912345678",
            direccion = "Av. Principal 123, Santiago"
        )
    )

    suspend fun login(email: String, password: String): Result<Usuario> {
        return try {
            val usuario = usuarios.find { it.email == email && it.password == password }
            if (usuario != null) {
                _currentUser.value = usuario
                Result.success(usuario)
            } else {
                Result.failure(Exception("Credenciales inválidas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registro(usuario: Usuario): Result<Usuario> {
        return try {
            if (usuarios.any { it.email == usuario.email }) {
                Result.failure(Exception("El email ya está registrado"))
            } else {
                val nuevoUsuario = usuario.copy(id = (usuarios.size + 1).toString())
                usuarios.add(nuevoUsuario)
                _currentUser.value = nuevoUsuario
                Result.success(nuevoUsuario)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        _currentUser.value = null
    }

    fun actualizarPerfil(usuario: Usuario): Result<Usuario> {
        return try {
            val index = usuarios.indexOfFirst { it.id == usuario.id }
            if (index != -1) {
                usuarios[index] = usuario
                _currentUser.value = usuario
                Result.success(usuario)
            } else {
                Result.failure(Exception("Usuario no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
