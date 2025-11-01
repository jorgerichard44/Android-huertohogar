package com.example.huertohogar.ui.catalogo



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huertohogar.data.model.ItemPedido
import com.example.huertohogar.data.model.Producto
import com.example.huertohogar.data.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CatalogoUiState(
    val productos: List<Producto> = emptyList(),
    val categoriaSeleccionada: String = "Todos",
    val carrito: List<ItemPedido> = emptyList(),
    val busqueda: String = ""
)

class CatalogoViewModel(
    private val productoRepository: ProductoRepository = ProductoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogoUiState())
    val uiState: StateFlow<CatalogoUiState> = _uiState

    init {
        cargarProductos()
    }

    private fun cargarProductos() {
        viewModelScope.launch {
            productoRepository.productos.collect { productos ->
                _uiState.value = _uiState.value.copy(
                    productos = filtrarProductos(productos)
                )
            }
        }
    }

    fun onCategoriaChange(categoria: String) {
        _uiState.value = _uiState.value.copy(categoriaSeleccionada = categoria)
        actualizarProductos()
    }

    fun onBusquedaChange(busqueda: String) {
        _uiState.value = _uiState.value.copy(busqueda = busqueda)
        actualizarProductos()
    }

    private fun actualizarProductos() {
        viewModelScope.launch {
            val productos = if (_uiState.value.busqueda.isNotEmpty()) {
                productoRepository.buscarProductos(_uiState.value.busqueda)
            } else {
                productoRepository.filtrarPorCategoria(_uiState.value.categoriaSeleccionada)
            }
            _uiState.value = _uiState.value.copy(productos = productos)
        }
    }

    private fun filtrarProductos(productos: List<Producto>): List<Producto> {
        return if (_uiState.value.categoriaSeleccionada == "Todos") {
            productos
        } else {
            productos.filter { it.categoria == _uiState.value.categoriaSeleccionada }
        }
    }

    fun agregarAlCarrito(producto: Producto) {
        val carritoActual = _uiState.value.carrito.toMutableList()
        val itemExistente = carritoActual.find { it.productoId == producto.id }

        if (itemExistente != null) {
            val index = carritoActual.indexOf(itemExistente)
            carritoActual[index] = itemExistente.copy(cantidad = itemExistente.cantidad + 1)
        } else {
            carritoActual.add(
                ItemPedido(
                    productoId = producto.id,
                    nombre = producto.nombre,
                    cantidad = 1,
                    precioUnitario = producto.precio
                )
            )
        }

        _uiState.value = _uiState.value.copy(carrito = carritoActual)
    }

    fun obtenerCantidadCarrito(): Int {
        return _uiState.value.carrito.sumOf { it.cantidad }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    onNavigateToCarrito: () -> Unit,
    onNavigateToPerfil: () -> Unit,
    viewModel: CatalogoViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var mostrarSnackbar by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    val categorias = listOf("Todos", "Verduras", "Frutas")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("HuertoHogar") },
                actions = {
                    // Botón Carrito con badge
                    BadgedBox(
                        badge = {
                            if (viewModel.obtenerCantidadCarrito() > 0) {
                                Badge {
                                    Text(viewModel.obtenerCantidadCarrito().toString())
                                }
                            }
                        }
                    ) {
                        IconButton(onClick = onNavigateToCarrito) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito")
                        }
                    }

                    // Botón Perfil
                    IconButton(onClick = onNavigateToPerfil) {
                        Icon(Icons.Default.Person, contentDescription = "Perfil")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
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
        ) {
            // Barra de búsqueda
            OutlinedTextField(
                value = uiState.busqueda,
                onValueChange = { viewModel.onBusquedaChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Buscar productos...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Buscar")
                },
                singleLine = true
            )

            // Filtros de categoría
            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categorias) { categoria ->
                    FilterChip(
                        selected = uiState.categoriaSeleccionada == categoria,
                        onClick = { viewModel.onCategoriaChange(categoria) },
                        label = { Text(categoria) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de productos
            if (uiState.productos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No se encontraron productos")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.productos) { producto ->
                        ProductoCard(
                            producto = producto,
                            onAgregarAlCarrito = {
                                viewModel.agregarAlCarrito(producto)
                                mostrarSnackbar = true
                            }
                        )
                    }
                }
            }
        }
    }

    // Mostrar Snackbar cuando se agrega al carrito
    LaunchedEffect(mostrarSnackbar) {
        if (mostrarSnackbar) {
            snackbarHostState.showSnackbar(
                message = "Producto agregado al carrito",
                duration = SnackbarDuration.Short
            )
            mostrarSnackbar = false
        }
    }
}

@Composable
fun ProductoCard(
    producto: Producto,
    onAgregarAlCarrito: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = producto.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = producto.descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Place,
                            contentDescription = "Origen",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = producto.origen,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Stock: ${producto.stock} unidades",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (producto.stock > 10) MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.error
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "$${String.format("%.0f", producto.precio)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onAgregarAlCarrito,
                        enabled = producto.disponible && producto.stock > 0
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Agregar",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Agregar")
                    }
                }
            }
        }
    }
}
