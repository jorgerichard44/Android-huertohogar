package com.example.huertohogar.ui.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.huertohogar.data.model.Usuario
import com.example.huertohogar.data.repository.UsuarioRepository
import com.example.huertohogar.ui.common.UIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PerfilViewModel(
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario

    private val _actualizarState = MutableStateFlow<UIState<Unit>>(UIState.Idle)
    val actualizarState: StateFlow<UIState<Unit>> = _actualizarState

    private val _nombre = MutableStateFlow("")
    val nombre: StateFlow<String> = _nombre

    private val _telefono = MutableStateFlow("")
    val telefono: StateFlow<String> = _telefono

    private val _direccion = MutableStateFlow("")
    val direccion: StateFlow<String> = _direccion

    fun cargarUsuario(usuarioId: Int) {
        viewModelScope.launch {
            usuarioRepository.obtenerUsuarioPorIdFlow(usuarioId).collectLatest { usuario ->
                _usuario.value = usuario
                usuario?.let {
                    _nombre.value = it.nombre
                    _telefono.value = it.telefono
                    _direccion.value = it.direccion
                }
            }
        }
    }

    fun onNombreChange(newNombre: String) {
        _nombre.value = newNombre
    }

    fun onTelefonoChange(newTelefono: String) {
        _telefono.value = newTelefono
    }

    fun onDireccionChange(newDireccion: String) {
        _direccion.value = newDireccion
    }

    fun actualizarPerfil() {
        val usuarioActual = _usuario.value ?: return

        viewModelScope.launch {
            _actualizarState.value = UIState.Loading
            try {
                val usuarioActualizado = usuarioActual.copy(
                    nombre = _nombre.value.trim(),
                    telefono = _telefono.value.trim(),
                    direccion = _direccion.value.trim()
                )

                usuarioRepository.actualizarUsuario(usuarioActualizado)
                _actualizarState.value = UIState.Success(Unit)
            } catch (e: Exception) {
                _actualizarState.value = UIState.Error(e.message ?: "Error al actualizar perfil")
            }
        }
    }

    fun resetState() {
        _actualizarState.value = UIState.Idle
    }
}

class PerfilViewModelFactory(
    private val usuarioRepository: UsuarioRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PerfilViewModel::class.java)) {
            return PerfilViewModel(usuarioRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
