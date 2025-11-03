package com.example.huertohogar.ui.pedidos

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
import com.example.huertohogar.data.model.DetallePedido
import com.example.huertohogar.data.model.Pedido
import com.example.huertohogar.ui.common.UIState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedidosScreen(
    viewModel: PedidosViewModel,
    usuarioId: Int,
    onNavigateBack: () -> Unit
) {
    val pedidos by viewModel.pedidos.collectAsState()
    val cancelarState by viewModel.cancelarState.collectAsState()

    var pedidoSeleccionado by remember { mutableStateOf<Pedido?>(null) }
    var mostrarDetalles by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(usuarioId) {
        viewModel.cargarPedidos(usuarioId)
    }

    LaunchedEffect(cancelarState) {
        when (val state = cancelarState) {
            is UIState.Success -> {
                snackbarHostState.showSnackbar("Pedido cancelado")
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
                title = { Text("Mis Pedidos") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (pedidos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No tienes pedidos",
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
                items(pedidos) { pedido ->
                    PedidoCard(
                        pedido = pedido,
                        onVerDetalles = {
                            pedidoSeleccionado = pedido
                            mostrarDetalles = true
                            viewModel.cargarDetallesPedido(pedido.id)
                        },
                        onCancelar = {
                            viewModel.cancelarPedido(pedido.id)
                        }
                    )
                }
            }
        }
    }

    if (mostrarDetalles && pedidoSeleccionado != null) {
        DetallePedidoDialog(
            viewModel = viewModel,
            pedido = pedidoSeleccionado!!,
            onDismiss = { mostrarDetalles = false }
        )
    }
}

@Composable
fun PedidoCard(
    pedido: Pedido,
    onVerDetalles: () -> Unit,
    onCancelar: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val fecha = dateFormat.format(Date(pedido.fechaPedido))

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Pedido #${pedido.id}",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = fecha,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Total: $${pedido.total}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Método de pago: ${pedido.metodoPago}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                EstadoChip(estado = pedido.estado)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Dirección: ${pedido.direccionEntrega}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onVerDetalles,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ver Detalles")
                }

                if (pedido.estado == "Pendiente") {
                    OutlinedButton(
                        onClick = onCancelar,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.Default.Cancel, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cancelar")
                    }
                }
            }
        }
    }
}

@Composable
fun EstadoChip(estado: String) {
    val color = when (estado) {
        "Pendiente" -> MaterialTheme.colorScheme.tertiary
        "En proceso" -> MaterialTheme.colorScheme.primary
        "Enviado" -> MaterialTheme.colorScheme.secondary
        "Entregado" -> MaterialTheme.colorScheme.primaryContainer
        "Cancelado" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.surface
    }

    AssistChip(
        onClick = { },
        label = { Text(estado) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = color
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallePedidoDialog(
    viewModel: PedidosViewModel,
    pedido: Pedido,
    onDismiss: () -> Unit
) {
    val detalles by viewModel.detallesPedido.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Detalles del Pedido #${pedido.id}") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (detalles.isEmpty()) {
                    CircularProgressIndicator()
                } else {
                    detalles.forEach { detalle ->
                        DetalleItem(detalle)
                        Divider() // ✅ CAMBIO AQUÍ
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total:",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "$${pedido.total}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
fun DetalleItem(detalle: DetallePedido) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = detalle.nombreProducto,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Cantidad: ${detalle.cantidad} x $${detalle.precioUnitario}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = "$${detalle.subtotal}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
