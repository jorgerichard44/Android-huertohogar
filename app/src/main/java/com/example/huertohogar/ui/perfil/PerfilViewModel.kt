package com.example.huertohogar.ui.perfil


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.huertohogar.data.model.Usuario
import com.example.huertohogar.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PerfilViewModel(private val repository: UsuarioRepository) : ViewModel() {

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario.asStateFlow()

    private val _actualizacionExitosa = MutableStateFlow(false)
    val actualizacionExitosa: StateFlow<Boolean> = _actualizacionExitosa.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun cargarUsuario(usuarioId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _usuario.value = repository.obtenerUsuarioPorId(usuarioId)
            _isLoading.value = false
        }
    }

    fun actualizarUsuario(usuario: Usuario) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.actualizarUsuario(usuario)
            _usuario.value = usuario
            _actualizacionExitosa.value = true
            _isLoading.value = false
        }
    }

    fun actualizarPassword(usuarioId: Int, nuevaPassword: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.actualizarPassword(usuarioId, nuevaPassword)
            _actualizacionExitosa.value = true
            _isLoading.value = false
        }
    }
}

class PerfilViewModelFactory(
    private val repository: UsuarioRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PerfilViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PerfilViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
