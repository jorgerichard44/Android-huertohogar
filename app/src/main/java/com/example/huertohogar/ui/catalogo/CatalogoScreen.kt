package com.example.huertohogar.ui.catalogo

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
import com.example.huertohogar.data.model.Producto
import com.example.huertohogar.ui.carrito.CarritoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    viewModel: CatalogoViewModel,
    carritoViewModel: CarritoViewModel,
    onNavigateToProductoForm: (Int?) -> Unit,
    onOpenDrawer: () -> Unit
) {
    val productosFiltrados by viewModel.productosFiltrados.collectAsState()
    val categoriaSeleccionada by viewModel.categoriaSeleccionada.collectAsState()
    val busqueda by viewModel.busqueda.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("HUERTO HOGAR") },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToProductoForm(null) }) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar producto")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Barra de búsqueda
            OutlinedTextField(
                value = busqueda,
                onValueChange = { viewModel.onBusquedaChange(it) },
                label = { Text("Buscar productos...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true
            )

            // Filtros por categoría
            ScrollableTabRow(
                selectedTabIndex = when (categoriaSeleccionada) {
                    "Todos" -> 0
                    "Verduras" -> 1
                    "Frutas" -> 2
                    "Hierbas" -> 3
                    else -> 0
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = categoriaSeleccionada == "Todos",
                    onClick = { viewModel.onCategoriaChange("Todos") },
                    text = { Text("Todos") }
                )
                Tab(
                    selected = categoriaSeleccionada == "Verduras",
                    onClick = { viewModel.onCategoriaChange("Verduras") },
                    text = { Text("Verduras") }
                )
                Tab(
                    selected = categoriaSeleccionada == "Frutas",
                    onClick = { viewModel.onCategoriaChange("Frutas") },
                    text = { Text("Frutas") }
                )
                Tab(
                    selected = categoriaSeleccionada == "Hierbas",
                    onClick = { viewModel.onCategoriaChange("Hierbas") },
                    text = { Text("Hierbas") }
                )
            }

            // Lista de productos
            if (productosFiltrados.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay productos disponibles")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(productosFiltrados) { producto ->
                        ProductoCard(
                            producto = producto,
                            onAgregarAlCarrito = {
                                carritoViewModel.agregarProducto(producto)
                                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                                    snackbarHostState.showSnackbar("${producto.nombre} agregado al carrito")
                                }
                            },
                            onEditar = { onNavigateToProductoForm(producto.id) },
                            onEliminar = { viewModel.eliminarProducto(producto) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoCard(
    producto: Producto,
    onAgregarAlCarrito: () -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
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
                        text = producto.nombre,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = producto.descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Categoría: ${producto.categoria}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Stock: ${producto.stock}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$${producto.precio}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (producto.esOrganico) {
                        AssistChip(
                            onClick = { },
                            label = { Text("Orgánico") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Eco,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAgregarAlCarrito,
                    modifier = Modifier.weight(1f),
                    enabled = producto.stock > 0
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Agregar")
                }

                IconButton(onClick = onEditar) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                }

                IconButton(onClick = onEliminar) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                }
            }
        }
    }
}
