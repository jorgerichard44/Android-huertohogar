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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huertohogar.data.model.Usuario
import com.example.huertohogar.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class PerfilUiState(
    val usuario: Usuario? = null,
    val modoEdicion: Boolean = false,
    val nombre: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val actualizacionExitosa: Boolean = false
)

class PerfilViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState

    init {
        cargarPerfil()
    }

    private fun cargarPerfil() {
        viewModelScope.launch {
            authRepository.currentUser.collect { usuario ->
                _uiState.value = _uiState.value.copy(
                    usuario = usuario,
                    nombre = usuario?.nombre ?: "",
                    telefono = usuario?.telefono ?: "",
                    direccion = usuario?.direccion ?: ""
                )
            }
        }
    }

    fun activarModoEdicion() {
        _uiState.value = _uiState.value.copy(modoEdicion = true)
    }

    fun cancelarEdicion() {
        val usuario = _uiState.value.usuario
        _uiState.value = _uiState.value.copy(
            modoEdicion = false,
            nombre = usuario?.nombre ?: "",
            telefono = usuario?.telefono ?: "",
            direccion = usuario?.direccion ?: "",
            errorMessage = null
        )
    }

    fun onNombreChange(nombre: String) {
        _uiState.value = _uiState.value.copy(nombre = nombre, errorMessage = null)
    }

    fun onTelefonoChange(telefono: String) {
        _uiState.value = _uiState.value.copy(telefono = telefono, errorMessage = null)
    }

    fun onDireccionChange(direccion: String) {
        _uiState.value = _uiState.value.copy(direccion = direccion, errorMessage = null)
    }

    fun guardarCambios() {
        viewModelScope.launch {
            val state = _uiState.value
            val usuario = state.usuario ?: return@launch

            if (state.nombre.isBlank()) {
                _uiState.value = state.copy(errorMessage = "El nombre es obligatorio")
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

            val usuarioActualizado = usuario.copy(
                nombre = state.nombre,
                telefono = state.telefono,
                direccion = state.direccion
            )

            val result = authRepository.actualizarPerfil(usuarioActualizado)

            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        modoEdicion = false,
                        actualizacionExitosa = true
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

    fun resetearActualizacion() {
        _uiState.value = _uiState.value.copy(actualizacionExitosa = false)
    }

    fun cerrarSesion() {
        authRepository.logout()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: PerfilViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var mostrarDialogoCerrarSesion by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.actualizacionExitosa) {
        if (uiState.actualizacionExitosa) {
            snackbarHostState.showSnackbar(
                message = "Perfil actualizado correctamente",
                duration = SnackbarDuration.Short
            )
            viewModel.resetearActualizacion()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (!uiState.modoEdicion) {
                        IconButton(onClick = { viewModel.activarModoEdicion() }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Avatar y nombre
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(100.dp),
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Avatar",
                            modifier = Modifier.size(60.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (!uiState.modoEdicion) {
                    Text(
                        text = uiState.usuario?.nombre ?: "",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = uiState.usuario?.email ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (uiState.modoEdicion) {
                // Modo edición
                Text(
                    text = "Editar Información",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

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

                OutlinedTextField(
                    value = uiState.telefono,
                    onValueChange = { viewModel.onTelefonoChange(it) },
                    label = { Text("Teléfono") },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = "Teléfono")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = uiState.direccion,
                    onValueChange = { viewModel.onDireccionChange(it) },
                    label = { Text("Dirección") },
                    leadingIcon = {
                        Icon(Icons.Default.Home, contentDescription = "Dirección")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3
                )

                if (uiState.errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.cancelarEdicion() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = { viewModel.guardarCambios() },
                        modifier = Modifier.weight(1f),
                        enabled = !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Guardar")
                        }
                    }
                }
            } else {
                // Modo visualización
                PerfilInfoCard(
                    titulo = "Información Personal",
                    items = listOf(
                        "Email" to (uiState.usuario?.email ?: ""),
                        "Teléfono" to (uiState.usuario?.telefono ?: ""),
                        "Dirección" to (uiState.usuario?.direccion ?: "")
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Opciones adicionales
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Opciones",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        PerfilOpcion(
                            icono = Icons.Default.ShoppingCart,
                            titulo = "Mis Pedidos",
                            onClick = { /* Navegar a pedidos */ }
                        )

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        PerfilOpcion(
                            icono = Icons.Default.Favorite,
                            titulo = "Favoritos",
                            onClick = { /* Navegar a favoritos */ }
                        )

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        PerfilOpcion(
                            icono = Icons.Default.Settings,
                            titulo = "Configuración",
                            onClick = { /* Navegar a configuración */ }
                        )

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        PerfilOpcion(
                            icono = Icons.Default.Info,
                            titulo = "Acerca de",
                            onClick = { /* Mostrar info */ }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botón cerrar sesión
                OutlinedButton(
                    onClick = { mostrarDialogoCerrarSesion = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar sesión")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cerrar Sesión")
                }
            }
        }
    }

    // Diálogo de confirmación para cerrar sesión
    if (mostrarDialogoCerrarSesion) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoCerrarSesion = false },
            icon = {
                Icon(Icons.Default.ExitToApp, contentDescription = null)
            },
            title = {
                Text("Cerrar Sesión")
            },
            text = {
                Text("¿Estás seguro que deseas cerrar sesión?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cerrarSesion()
                        mostrarDialogoCerrarSesion = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Cerrar Sesión")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarDialogoCerrarSesion = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun PerfilInfoCard(
    titulo: String,
    items: List<Pair<String, String>>
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            items.forEach { (label, value) ->
                Column {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    if (items.last() != (label to value)) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun PerfilOpcion(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    titulo: String,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    icono,
                    contentDescription = titulo,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = "Ir",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
