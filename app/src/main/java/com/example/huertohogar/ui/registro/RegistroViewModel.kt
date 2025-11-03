package com.example.huertohogar.ui.registro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.huertohogar.data.model.Usuario
import com.example.huertohogar.data.repository.UsuarioRepository
import com.example.huertohogar.ui.common.UIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegistroViewModel(
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _registroState = MutableStateFlow<UIState<Long>>(UIState.Idle)
    val registroState: StateFlow<UIState<Long>> = _registroState

    private val _nombre = MutableStateFlow("")
    val nombre: StateFlow<String> = _nombre

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _confirmarPassword = MutableStateFlow("")
    val confirmarPassword: StateFlow<String> = _confirmarPassword

    private val _telefono = MutableStateFlow("")
    val telefono: StateFlow<String> = _telefono

    private val _direccion = MutableStateFlow("")
    val direccion: StateFlow<String> = _direccion

    private val _nombreError = MutableStateFlow<String?>(null)
    val nombreError: StateFlow<String?> = _nombreError

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError

    private val _confirmarPasswordError = MutableStateFlow<String?>(null)
    val confirmarPasswordError: StateFlow<String?> = _confirmarPasswordError

    fun onNombreChange(newNombre: String) {
        _nombre.value = newNombre
        _nombreError.value = null
    }

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
        _emailError.value = null
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
        _passwordError.value = null
    }

    fun onConfirmarPasswordChange(newPassword: String) {
        _confirmarPassword.value = newPassword
        _confirmarPasswordError.value = null
    }

    fun onTelefonoChange(newTelefono: String) {
        _telefono.value = newTelefono
    }

    fun onDireccionChange(newDireccion: String) {
        _direccion.value = newDireccion
    }

    fun registrar() {
        if (!validateFields()) {
            return
        }

        viewModelScope.launch {
            _registroState.value = UIState.Loading

            try {
                val usuario = Usuario(
                    nombre = _nombre.value.trim(),
                    email = _email.value.trim(),
                    password = _password.value,
                    telefono = _telefono.value.trim(),
                    direccion = _direccion.value.trim()
                )

                val result = usuarioRepository.registrar(usuario)

                result.onSuccess { id ->
                    _registroState.value = UIState.Success(id)
                }.onFailure { error ->
                    _registroState.value = UIState.Error(error.message ?: "Error al registrar")
                }
            } catch (e: Exception) {
                _registroState.value = UIState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    private fun validateFields(): Boolean {
        var isValid = true

        if (_nombre.value.isBlank()) {
            _nombreError.value = "El nombre es requerido"
            isValid = false
        }

        if (_email.value.isBlank()) {
            _emailError.value = "El email es requerido"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(_email.value).matches()) {
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

        if (_confirmarPassword.value.isBlank()) {
            _confirmarPasswordError.value = "Confirma tu contraseña"
            isValid = false
        } else if (_password.value != _confirmarPassword.value) {
            _confirmarPasswordError.value = "Las contraseñas no coinciden"
            isValid = false
        }

        return isValid
    }

    fun resetState() {
        _registroState.value = UIState.Idle
    }
}

class RegistroViewModelFactory(
    private val usuarioRepository: UsuarioRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegistroViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegistroViewModel(usuarioRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
