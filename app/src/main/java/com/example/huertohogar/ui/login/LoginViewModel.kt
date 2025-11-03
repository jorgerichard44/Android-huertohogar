package com.example.huertohogar.ui.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.huertohogar.data.model.Usuario
import com.example.huertohogar.data.repository.UsuarioRepository
import com.example.huertohogar.ui.common.UIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<UIState<Usuario>>(UIState.Idle)
    val loginState: StateFlow<UIState<Usuario>> = _loginState

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
        _emailError.value = null
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
        _passwordError.value = null
    }

    fun login() {
        if (!validateFields()) {
            return
        }

        viewModelScope.launch {
            _loginState.value = UIState.Loading

            try {
                val usuario = usuarioRepository.login(_email.value.trim(), _password.value)

                if (usuario != null) {
                    _usuarioActual.value = usuario
                    _loginState.value = UIState.Success(usuario)
                } else {
                    _loginState.value = UIState.Error("Email o contraseña incorrectos")
                }
            } catch (e: Exception) {
                _loginState.value = UIState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    private fun validateFields(): Boolean {
        var isValid = true

        if (_email.value.isBlank()) {
            _emailError.value = "El email es requerido"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(_email.value).matches()) {
            _emailError.value = "Email inválido"
            isValid = false
        }

        if (_password.value.isBlank()) {
            _passwordError.value = "La contraseña es requerida"
            isValid = false
        } else if (_password.value.length < 6) {
            _passwordError.value = "La contraseña debe tener al menos 6 caracteres"
            isValid = false
        }

        return isValid
    }

    fun logout() {
        _usuarioActual.value = null
        _loginState.value = UIState.Idle
        _email.value = ""
        _password.value = ""
    }

    fun resetState() {
        _loginState.value = UIState.Idle
    }
}

class LoginViewModelFactory(
    private val usuarioRepository: UsuarioRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(usuarioRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
