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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.huertohogar.ui.common.UIState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    viewModel: RegistroViewModel,
    onNavigateToLogin: () -> Unit,
    onRegistroExitoso: () -> Unit
) {
    val registroState by viewModel.registroState.collectAsState()
    val nombre by viewModel.nombre.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val confirmarPassword by viewModel.confirmarPassword.collectAsState()
    val telefono by viewModel.telefono.collectAsState()
    val direccion by viewModel.direccion.collectAsState()

    val nombreError by viewModel.nombreError.collectAsState()
    val emailError by viewModel.emailError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()
    val confirmarPasswordError by viewModel.confirmarPasswordError.collectAsState()

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmarPasswordVisible by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(registroState) {
        when (val state = registroState) {
            is UIState.Success -> {
                snackbarHostState.showSnackbar("Registro exitoso")
                onRegistroExitoso()
                viewModel.resetState()
            }
            is UIState.Error -> {
                snackbarHostState.showSnackbar(
                    message = state.message,
                    duration = SnackbarDuration.Short
                )
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Registro") },
                navigationIcon = {
                    IconButton(onClick = onNavigateToLogin) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PersonAdd,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Crear Cuenta",
                style = MaterialTheme.typography.headlineMedium
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = { viewModel.onNombreChange(it) },
                label = { Text("Nombre completo") },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                },
                isError = nombreError != null,
                supportingText = {
                    nombreError?.let { Text(it) }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text("Email") },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = emailError != null,
                supportingText = {
                    emailError?.let { Text(it) }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = { Text("Contraseña") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null)
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility
                            else Icons.Default.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = passwordError != null,
                supportingText = {
                    passwordError?.let { Text(it) }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = confirmarPassword,
                onValueChange = { viewModel.onConfirmarPasswordChange(it) },
                label = { Text("Confirmar contraseña") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null)
                },
                trailingIcon = {
                    IconButton(onClick = { confirmarPasswordVisible = !confirmarPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmarPasswordVisible) Icons.Default.Visibility
                            else Icons.Default.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                visualTransformation = if (confirmarPasswordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = confirmarPasswordError != null,
                supportingText = {
                    confirmarPasswordError?.let { Text(it) }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = telefono,
                onValueChange = { viewModel.onTelefonoChange(it) },
                label = { Text("Teléfono (opcional)") },
                leadingIcon = {
                    Icon(Icons.Default.Phone, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = direccion,
                onValueChange = { viewModel.onDireccionChange(it) },
                label = { Text("Dirección (opcional)") },
                leadingIcon = {
                    Icon(Icons.Default.Home, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2
            )

            Button(
                onClick = { viewModel.registrar() },
                modifier = Modifier.fillMaxWidth(),
                enabled = registroState !is UIState.Loading
            ) {
                if (registroState is UIState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Registrarse")
                }
            }

            TextButton(
                onClick = onNavigateToLogin,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }
        }
    }
}
