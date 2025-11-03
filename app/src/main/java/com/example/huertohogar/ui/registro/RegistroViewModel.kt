package com.example.huertohogar.ui.registro


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.huertohogar.data.model.Usuario
import com.example.huertohogar.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegistroViewModel(private val repository: UsuarioRepository) : ViewModel() {

    private val _registroExitoso = MutableStateFlow(false)
    val registroExitoso: StateFlow<Boolean> = _registroExitoso.asStateFlow()

    private val _errorMensaje = MutableStateFlow<String?>(null)
    val errorMensaje: StateFlow<String?> = _errorMensaje.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun registrarUsuario(
        nombre: String,
        apellido: String,
        email: String,
        password: String,
        telefono: String,
        direccion: String,
        ciudad: String,
        region: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMensaje.value = null

            try {
                // Validar que el email no exista
                if (repository.existeEmail(email)) {
                    _errorMensaje.value = "El email ya está registrado"
                    _isLoading.value = false
                    return@launch
                }

                val usuario = Usuario(
                    nombre = nombre,
                    apellido = apellido,
                    email = email,
                    password = password,
                    telefono = telefono,
                    direccion = direccion,
                    ciudad = ciudad,
                    region = region
                )

                repository.registrarUsuario(usuario)
                _registroExitoso.value = true

            } catch (e: Exception) {
                _errorMensaje.value = "Error al registrar: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetearEstado() {
        _registroExitoso.value = false
        _errorMensaje.value = null
    }
}

class RegistroViewModelFactory(
    private val repository: UsuarioRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegistroViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegistroViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
