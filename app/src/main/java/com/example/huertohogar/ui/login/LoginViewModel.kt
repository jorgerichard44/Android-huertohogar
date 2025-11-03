package com.example.huertohogar.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.huertohogar.data.model.Usuario
import com.example.huertohogar.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val usuario: Usuario) : LoginState()
    data class Error(val mensaje: String) : LoginState()
}

class LoginViewModel(private val repository: UsuarioRepository) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            try {
                // Validaciones básicas
                if (email.isBlank() || password.isBlank()) {
                    _loginState.value = LoginState.Error("Por favor completa todos los campos")
                    return@launch
                }

                if (!email.contains("@")) {
                    _loginState.value = LoginState.Error("Email inválido")
                    return@launch
                }

                // Intentar login
                val usuario = repository.login(email, password)

                if (usuario != null) {
                    _usuarioActual.value = usuario
                    _loginState.value = LoginState.Success(usuario)
                } else {
                    _loginState.value = LoginState.Error("Email o contraseña incorrectos")
                }

            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Error al iniciar sesión: ${e.message}")
            }
        }
    }

    fun logout() {
        _usuarioActual.value = null
        _loginState.value = LoginState.Idle
    }

    fun resetearEstado() {
        _loginState.value = LoginState.Idle
    }
}

class LoginViewModelFactory(
    private val repository: UsuarioRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
