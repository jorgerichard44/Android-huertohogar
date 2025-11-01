package com.example.huertohogar.ui.carrito



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huertohogar.data.model.ItemPedido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class CarritoUiState(
    val items: List<ItemPedido> = emptyList(),
    val mostrarDialogoConfirmacion: Boolean = false,
    val pedidoRealizado: Boolean = false
)

class CarritoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CarritoUiState())
    val uiState: StateFlow<CarritoUiState> = _uiState

    init {
        // Aquí cargaríamos los items del carrito desde el ViewModel del catálogo
        // Por simplicidad, usaremos datos de ejemplo
        cargarCarrito()
    }

    private fun cargarCarrito() {
        // En una implementación real, esto vendría del repositorio compartido
        _uiState.value = _uiState.value.copy(
            items = listOf(
                ItemPedido(
                    productoId = "1",
                    nombre = "Tomates Orgánicos",
                    cantidad = 2,
                    precioUnitario = 2500.0
                ),
                ItemPedido(
                    productoId = "2",
                    nombre = "Lechugas Hidropónicas",
                    cantidad = 1,
                    precioUnitario = 1800.0
                )
            )
        )
    }

    fun aumentarCantidad(item: ItemPedido) {
        val itemsActualizados = _uiState.value.items.map {
            if (it.productoId == item.productoId) {
                it.copy(cantidad = it.cantidad + 1)
            } else {
                it
            }
        }
        _uiState.value = _uiState.value.copy(items = itemsActualizados)
    }

    fun disminuirCantidad(item: ItemPedido) {
        val itemsActualizados = _uiState.value.items.mapNotNull {
            if (it.productoId == item.productoId) {
                if (it.cantidad > 1) {
                    it.copy(cantidad = it.cantidad - 1)
                } else {
                    null // Eliminar el item si la cantidad llega a 0
                }
            } else {
                it
            }
        }
        _uiState.value = _uiState.value.copy(items = itemsActualizados)
    }

    fun eliminarItem(item: ItemPedido) {
        val itemsActualizados = _uiState.value.items.filter {
            it.productoId != item.productoId
        }
        _uiState.value = _uiState.value.copy(items = itemsActualizados)
    }

    fun calcularSubtotal(): Double {
        return _uiState.value.items.sumOf { it.cantidad * it.precioUnitario }
    }

    fun calcularEnvio(): Double {
        return if (_uiState.value.items.isNotEmpty()) 2500.0 else 0.0
    }

    fun calcularTotal(): Double {
        return calcularSubtotal() + calcularEnvio()
    }

    fun mostrarDialogoConfirmacion() {
        _uiState.value = _uiState.value.copy(mostrarDialogoConfirmacion = true)
    }

    fun ocultarDialogoConfirmacion() {
        _uiState.value = _uiState.value.copy(mostrarDialogoConfirmacion = false)
    }

    fun realizarPedido() {
        // Aquí se implementaría la lógica para enviar el pedido al backend
        _uiState.value = _uiState.value.copy(
            pedidoRealizado = true,
            mostrarDialogoConfirmacion = false,
            items = emptyList()
        )
    }

    fun resetearPedido() {
        _uiState.value = _uiState.value.copy(pedidoRealizado = false)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(
    onNavigateBack: () -> Unit,
    viewModel: CarritoViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.pedidoRealizado) {
        if (uiState.pedidoRealizado) {
            snackbarHostState.showSnackbar(
                message = "¡Pedido realizado con éxito!",
                duration = SnackbarDuration.Long
            )
            viewModel.resetearPedido()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Carrito") },
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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (uiState.items.isNotEmpty()) {
                CarritoBottomBar(
                    subtotal = viewModel.calcularSubtotal(),
                    envio = viewModel.calcularEnvio(),
                    total = viewModel.calcularTotal(),
                    onRealizarPedido = { viewModel.mostrarDialogoConfirmacion() }
                )
            }
        }
    ) { paddingValues ->
        if (uiState.items.isEmpty()) {
            // Carrito vacío
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = "Carrito vacío",
                        modifier = Modifier.size(100.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Tu carrito está vacío",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Agrega productos para comenzar",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = onNavigateBack) {
                        Text("Ir al catálogo")
                    }
                }
            }
        } else {
            // Lista de items en el carrito
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.items) { item ->
                    CarritoItemCard(
                        item = item,
                        onAumentar = { viewModel.aumentarCantidad(item) },
                        onDisminuir = { viewModel.disminuirCantidad(item) },
                        onEliminar = { viewModel.eliminarItem(item) }
                    )
                }

                // Espacio adicional para el bottom bar
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    // Diálogo de confirmación
    if (uiState.mostrarDialogoConfirmacion) {
        AlertDialog(
            onDismissRequest = { viewModel.ocultarDialogoConfirmacion() },
            icon = {
                Icon(Icons.Default.ShoppingCart, contentDescription = null)
            },
            title = {
                Text("Confirmar Pedido")
            },
            text = {
                Column {
                    Text("¿Deseas confirmar tu pedido?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Total: $${String.format("%.0f", viewModel.calcularTotal())}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.realizarPedido() }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.ocultarDialogoConfirmacion() }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun CarritoItemCard(
    item: ItemPedido,
    onAumentar: () -> Unit,
    onDisminuir: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$${String.format("%.0f", item.precioUnitario)} c/u",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Subtotal: $${String.format("%.0f", item.cantidad * item.precioUnitario)}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                // Controles de cantidad
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onDisminuir,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Remove,
                            contentDescription = "Disminuir",
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = item.cantidad.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = onAumentar,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Aumentar",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botón eliminar
                TextButton(
                    onClick = onEliminar,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Eliminar")
                }
            }
        }
    }
}

@Composable
fun CarritoBottomBar(
    subtotal: Double,
    envio: Double,
    total: Double,
    onRealizarPedido: () -> Unit
) {
    Surface(
        shadowElevation = 8.dp,
        tonalElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Subtotal:", style = MaterialTheme.typography.bodyMedium)
                Text(
                    "$${String.format("%.0f", subtotal)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Envío:", style = MaterialTheme.typography.bodyMedium)
                Text(
                    "$${String.format("%.0f", envio)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Total:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "$${String.format("%.0f", total)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onRealizarPedido,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Realizar Pedido")
            }
        }
    }
}
