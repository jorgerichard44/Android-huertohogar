package com.example.huertohogar.data.dao

import androidx.room.*
import com.example.huertohogar.data.model.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    // ✅ REGISTRAR un usuario
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun registrarUsuario(usuario: Usuario): Long

    // ✅ ACTUALIZAR usuario
    @Update
    suspend fun actualizarUsuario(usuario: Usuario)

    // ✅ ELIMINAR usuario
    @Delete
    suspend fun eliminarUsuario(usuario: Usuario)

    // ✅ OBTENER usuario por ID
    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun obtenerUsuarioPorId(id: Int): Usuario?

    // ✅ LOGIN: Verificar credenciales
    @Query("SELECT * FROM usuarios WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): Usuario?

    // ✅ VERIFICAR si el email ya existe
    @Query("SELECT EXISTS(SELECT 1 FROM usuarios WHERE email = :email)")
    suspend fun existeEmail(email: String): Boolean

    // ✅ OBTENER usuario por email
    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun obtenerUsuarioPorEmail(email: String): Usuario?

    // ✅ OBTENER todos los usuarios (Flow para actualizaciones automáticas)
    @Query("SELECT * FROM usuarios ORDER BY nombre ASC")
    fun obtenerTodosLosUsuarios(): Flow<List<Usuario>>

    // ✅ BUSCAR usuarios por nombre o apellido
    @Query("""
        SELECT * FROM usuarios 
        WHERE nombre LIKE '%' || :query || '%' 
        OR apellido LIKE '%' || :query || '%'
        ORDER BY nombre ASC
    """)
    fun buscarUsuarios(query: String): Flow<List<Usuario>>

    // ✅ ACTUALIZAR contraseña
    @Query("UPDATE usuarios SET password = :nuevaPassword WHERE id = :usuarioId")
    suspend fun actualizarPassword(usuarioId: Int, nuevaPassword: String)

    // ✅ ACTUALIZAR datos de perfil
    @Query("""
        UPDATE usuarios 
        SET nombre = :nombre, 
            apellido = :apellido, 
            telefono = :telefono, 
            direccion = :direccion, 
            ciudad = :ciudad, 
            region = :region 
        WHERE id = :usuarioId
    """)
    suspend fun actualizarPerfil(
        usuarioId: Int,
        nombre: String,
        apellido: String,
        telefono: String,
        direccion: String,
        ciudad: String,
        region: String
    )

    // ✅ CONTAR usuarios registrados
    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contarUsuarios(): Int

    // ✅ ELIMINAR todos los usuarios (útil para testing)
    @Query("DELETE FROM usuarios")
    suspend fun eliminarTodosLosUsuarios()
}
