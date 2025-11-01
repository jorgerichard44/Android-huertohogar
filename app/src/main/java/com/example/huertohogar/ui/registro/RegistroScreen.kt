package com.example.huertohogar.ui.registro



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huertohogar.data.model.Usuario
import com.example.huertohogar.data.repository.AuthRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class RegistroUiState(
    val nombre: String = "",
    val email: String = "",
    val password: String = "",
    val confirmarPassword: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val registroExitoso: Boolean = false
)

class RegistroViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState: StateFlow<RegistroUiState> = _uiState

    fun onNombreChange(nombre: String) {
        _uiState.value = _uiState.value.copy(nombre = nombre, errorMessage = null)
    }

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email, errorMessage = null)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password, errorMessage = null)
    }

    fun onConfirmarPasswordChange(confirmarPassword: String) {
        _uiState.value = _uiState.value.copy(confirmarPassword = confirmarPassword, errorMessage = null)
    }

    fun onTelefonoChange(telefono: String) {
        _uiState.value = _uiState.value.copy(telefono = telefono, errorMessage = null)
    }

    fun onDireccionChange(direccion: String) {
        _uiState.value = _uiState.value.copy(direccion = direccion, errorMessage = null)
    }

    fun registrar() {
        viewModelScope.launch {
            val state = _uiState.value

            // Validaciones
            if (state.nombre.isBlank()) {
                _uiState.value = state.copy(errorMessage = "El nombre es obligatorio")
                return@launch
            }

            if (state.email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
                _uiState.value = state.copy(errorMessage = "Email inválido")
                return@launch
            }

            if (state.password.length < 6) {
                _uiState.value = state.copy(errorMessage = "La contraseña debe tener al menos 6 caracteres")
                return@launch
            }

            if (state.password != state.confirmarPassword) {
                _uiState.value = state.copy(errorMessage = "Las contraseñas no coinciden")
                return@launch
            }

            if (state.telefono.isBlank()) {
                _uiState.value = state.copy(errorMessage = "El teléfono es obligatorio")
                return@launch
            }

            if (state.direccion.isBlank()) {
                _uiState.value = state.copy(errorMessage = "La dirección es obligatoria")
                return@launch
            }

            _uiState.value = state.copy(isLoading = true, errorMessage = null)

            val nuevoUsuario = Usuario(
                nombre = state.nombre,
                email = state.email,
                password = state.password,
                telefono = state.telefono,
                direccion = state.direccion
            )

            val result = authRepository.registro(nuevoUsuario)

            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        registroExitoso = true
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Error desconocido"
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    onNavigateBack: () -> Unit,
    onRegistroExitoso: () -> Unit,
    viewModel: RegistroViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmarPasswordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.registroExitoso) {
        if (uiState.registroExitoso) {
            onRegistroExitoso()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Crear Cuenta",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Completa tus datos para registrarte",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campo Nombre
            OutlinedTextField(
                value = uiState.nombre,
                onValueChange = { viewModel.onNombreChange(it) },
                label = { Text("Nombre Completo") },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = "Nombre")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Email
            OutlinedTextField(
                value = uiState.email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text("Email") },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = "Email")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Teléfono
            OutlinedTextField(
                value = uiState.telefono,
                onValueChange = { viewModel.onTelefonoChange(it) },
                label = { Text("Teléfono") },
                leadingIcon = {
                    Icon(Icons.Default.Phone, contentDescription = "Teléfono")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("+56912345678") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Dirección
            OutlinedTextField(
                value = uiState.direccion,
                onValueChange = { viewModel.onDireccionChange(it) },
                label = { Text("Dirección de Entrega") },
                leadingIcon = {
                    Icon(Icons.Default.Home, contentDescription = "Dirección")
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Password
            OutlinedTextField(
                value = uiState.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = { Text("Contraseña") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = "Contraseña")
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility
                            else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Ocultar contraseña"
                            else "Mostrar contraseña"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Confirmar Password
            OutlinedTextField(
                value = uiState.confirmarPassword,
                onValueChange = { viewModel.onConfirmarPasswordChange(it) },
                label = { Text("Confirmar Contraseña") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = "Confirmar Contraseña")
                },
                trailingIcon = {
                    IconButton(onClick = { confirmarPasswordVisible = !confirmarPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmarPasswordVisible) Icons.Default.Visibility
                            else Icons.Default.VisibilityOff,
                            contentDescription = if (confirmarPasswordVisible) "Ocultar contraseña"
                            else "Mostrar contraseña"
                        )
                    }
                },
                visualTransformation = if (confirmarPasswordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Mensaje de error
            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Registrar
            Button(
                onClick = { viewModel.registrar() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Registrarse")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateBack) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }
        }
    }
}
