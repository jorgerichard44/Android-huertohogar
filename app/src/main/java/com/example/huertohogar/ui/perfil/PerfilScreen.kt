package com.example.huertohogar.ui.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.huertohogar.ui.common.UIState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    viewModel: PerfilViewModel,
    usuarioId: Int,
    onNavigateBack: () -> Unit
) {
    val usuario by viewModel.usuario.collectAsState()
    val actualizarState by viewModel.actualizarState.collectAsState()
    val nombre by viewModel.nombre.collectAsState()
    val telefono by viewModel.telefono.collectAsState()
    val direccion by viewModel.direccion.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(usuarioId) {
        viewModel.cargarUsuario(usuarioId)
    }

    LaunchedEffect(actualizarState) {
        when (val state = actualizarState) {
            is UIState.Success -> {
                snackbarHostState.showSnackbar("Perfil actualizado exitosamente")
                viewModel.resetState()
            }
            is UIState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            usuario?.let { user ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Información de la cuenta",
                            style = MaterialTheme.typography.titleLarge
                        )

                        HorizontalDivider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Email",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = user.email,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }

                        if (user.esAdmin) {
                            AssistChip(
                                onClick = { },
                                label = { Text("Administrador") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.AdminPanelSettings,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Editar información",
                            style = MaterialTheme.typography.titleLarge
                        )

                        OutlinedTextField(
                            value = nombre,
                            onValueChange = { viewModel.onNombreChange(it) },
                            label = { Text("Nombre completo") },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = telefono,
                            onValueChange = { viewModel.onTelefonoChange(it) },
                            label = { Text("Teléfono") },
                            leadingIcon = {
                                Icon(Icons.Default.Phone, contentDescription = null)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = direccion,
                            onValueChange = { viewModel.onDireccionChange(it) },
                            label = { Text("Dirección") },
                            leadingIcon = {
                                Icon(Icons.Default.Home, contentDescription = null)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )

                        Button(
                            onClick = { viewModel.actualizarPerfil() },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = actualizarState !is UIState.Loading
                        ) {
                            if (actualizarState is UIState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Icon(Icons.Default.Save, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Guardar Cambios")
                            }
                        }
                    }
                }
            }
        }
    }
}
