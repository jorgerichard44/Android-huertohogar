package com.example.huertohogar.data.repository


import com.example.huertohogar.data.dao.UsuarioDao
import com.example.huertohogar.data.model.Usuario
import kotlinx.coroutines.flow.Flow

class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    // ✅ Flow de todos los usuarios
    val todosLosUsuarios: Flow<List<Usuario>> = usuarioDao.obtenerTodosLosUsuarios()

    // ✅ Registrar nuevo usuario
    suspend fun registrarUsuario(usuario: Usuario): Long {
        return try {
            usuarioDao.registrarUsuario(usuario)
        } catch (e: Exception) {
            throw Exception("Error al registrar usuario: ${e.message}")
        }
    }

    // ✅ Login - Verificar credenciales
    suspend fun login(email: String, password: String): Usuario? {
        return usuarioDao.login(email, password)
    }

    // ✅ Verificar si email existe
    suspend fun existeEmail(email: String): Boolean {
        return usuarioDao.existeEmail(email)
    }

    // ✅ Obtener usuario por ID
    suspend fun obtenerUsuarioPorId(id: Int): Usuario? {
        return usuarioDao.obtenerUsuarioPorId(id)
    }

    // ✅ Obtener usuario por email
    suspend fun obtenerUsuarioPorEmail(email: String): Usuario? {
        return usuarioDao.obtenerUsuarioPorEmail(email)
    }

    // ✅ Actualizar usuario completo
    suspend fun actualizarUsuario(usuario: Usuario) {
        usuarioDao.actualizarUsuario(usuario)
    }

    // ✅ Actualizar solo el perfil (sin password)
    suspend fun actualizarPerfil(
        usuarioId: Int,
        nombre: String,
        apellido: String,
        telefono: String,
        direccion: String,
        ciudad: String,
        region: String
    ) {
        usuarioDao.actualizarPerfil(
            usuarioId = usuarioId,
            nombre = nombre,
            apellido = apellido,
            telefono = telefono,
            direccion = direccion,
            ciudad = ciudad,
            region = region
        )
    }

    // ✅ Actualizar contraseña
    suspend fun actualizarPassword(usuarioId: Int, nuevaPassword: String) {
        usuarioDao.actualizarPassword(usuarioId, nuevaPassword)
    }

    // ✅ Eliminar usuario
    suspend fun eliminarUsuario(usuario: Usuario) {
        usuarioDao.eliminarUsuario(usuario)
    }

    // ✅ Buscar usuarios
    fun buscarUsuarios(query: String): Flow<List<Usuario>> {
        return usuarioDao.buscarUsuarios(query)
    }

    // ✅ Contar usuarios
    suspend fun contarUsuarios(): Int {
        return usuarioDao.contarUsuarios()
    }

    // ✅ Validar datos de registro
    fun validarRegistro(
        nombre: String,
        apellido: String,
        email: String,
        password: String,
        telefono: String
    ): String? {
        return when {
            nombre.isBlank() -> "El nombre es obligatorio"
            apellido.isBlank() -> "El apellido es obligatorio"
            email.isBlank() -> "El email es obligatorio"
            !email.contains("@") -> "Email inválido"
            password.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            telefono.isBlank() -> "El teléfono es obligatorio"
            else -> null // ✅ Validación exitosa
        }
    }
}
