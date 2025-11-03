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
import androidx.compose.ui.unit.dp
import com.example.huertohogar.data.model.ItemCarrito
import com.example.huertohogar.ui.common.UIState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(
    viewModel: CarritoViewModel,
    usuarioId: Int,
    onNavigateBack: () -> Unit
) {
    val carrito by viewModel.carrito.collectAsState()
    val total by viewModel.total.collectAsState()
    val pedidoState by viewModel.pedidoState.collectAsState()

    var mostrarDialogoPedido by remember { mutableStateOf(false) }
    var direccionEntrega by remember { mutableStateOf("") }
    var metodoPago by remember { mutableStateOf("Tarjeta") }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(pedidoState) {
        when (val state = pedidoState) {
            is UIState.Success -> {
                snackbarHostState.showSnackbar("Pedido creado exitosamente")
                onNavigateBack()
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
                title = { Text("Carrito de Compras") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (carrito.isNotEmpty()) {
                        IconButton(onClick = { viewModel.limpiarCarrito() }) {
                            Icon(Icons.Default.Delete, contentDescription = "Limpiar carrito")
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (carrito.isNotEmpty()) {
                Surface(
                    shadowElevation = 8.dp
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
                            Text(
                                text = "Total:",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "$$total",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { mostrarDialogoPedido = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Realizar Pedido")
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (carrito.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Tu carrito está vacío",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(carrito) { item ->
                    ItemCarritoCard(
                        item = item,
                        onCantidadChange = { nuevaCantidad ->
                            viewModel.actualizarCantidad(item.producto.id, nuevaCantidad)
                        },
                        onEliminar = {
                            viewModel.eliminarProducto(item.producto.id)
                        }
                    )
                }
            }
        }
    }

    if (mostrarDialogoPedido) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoPedido = false },
            title = { Text("Confirmar Pedido") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = direccionEntrega,
                        onValueChange = { direccionEntrega = it },
                        label = { Text("Dirección de entrega") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Método de pago:")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = metodoPago == "Tarjeta",
                            onClick = { metodoPago = "Tarjeta" },
                            label = { Text("Tarjeta") }
                        )
                        FilterChip(
                            selected = metodoPago == "Transferencia",
                            onClick = { metodoPago = "Transferencia" },
                            label = { Text("Transferencia") }
                        )
                        FilterChip(
                            selected = metodoPago == "Efectivo",
                            onClick = { metodoPago = "Efectivo" },
                            label = { Text("Efectivo") }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.crearPedido(usuarioId, direccionEntrega, metodoPago)
                        mostrarDialogoPedido = false
                    },
                    enabled = direccionEntrega.isNotBlank()
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoPedido = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun ItemCarritoCard(
    item: ItemCarrito,
    onCantidadChange: (Int) -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.producto.nombre,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "$${item.producto.precio} c/u",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Subtotal: $${item.subtotal}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { onCantidadChange(item.cantidad - 1) }
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Disminuir")
                }

                Text(
                    text = item.cantidad.toString(),
                    style = MaterialTheme.typography.titleMedium
                )

                IconButton(
                    onClick = { onCantidadChange(item.cantidad + 1) }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Aumentar")
                }

                IconButton(onClick = onEliminar) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                }
            }
        }
    }
}
