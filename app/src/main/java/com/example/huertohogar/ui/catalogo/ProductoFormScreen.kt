package com.example.huertohogar.ui.catalogo

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
import androidx.compose.ui.unit.dp
import com.example.huertohogar.data.model.Producto
import com.example.huertohogar.ui.common.UIState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoFormScreen(
    viewModel: CatalogoViewModel,
    productoId: Int?,
    onNavigateBack: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("Verduras") }
    var stock by remember { mutableStateOf("") }
    var esOrganico by remember { mutableStateOf(true) }
    var expanded by remember { mutableStateOf(false) }

    val productoState by viewModel.productoState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(productoId) {
        productoId?.let { id ->
            viewModel.obtenerProductoPorId(id)?.let { producto ->
                nombre = producto.nombre
                descripcion = producto.descripcion
                precio = producto.precio.toString()
                categoria = producto.categoria
                stock = producto.stock.toString()
                esOrganico = producto.esOrganico
            }
        }
    }

    LaunchedEffect(productoState) {
        when (val state = productoState) {
            is UIState.Success -> {
                snackbarHostState.showSnackbar("Producto guardado exitosamente")
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
                title = { Text(if (productoId == null) "Nuevo Producto" else "Editar Producto") },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del producto") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            OutlinedTextField(
                value = precio,
                onValueChange = { precio = it },
                label = { Text("Precio") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = categoria,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    listOf("Verduras", "Frutas", "Hierbas").forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                categoria = cat
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = stock,
                onValueChange = { stock = it },
                label = { Text("Stock") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = esOrganico,
                    onCheckedChange = { esOrganico = it }
                )
                Text("Producto orgánico")
            }

            Button(
                onClick = {
                    val producto = Producto(
                        id = productoId ?: 0,
                        nombre = nombre,
                        descripcion = descripcion,
                        precio = precio.toDoubleOrNull() ?: 0.0,
                        categoria = categoria,
                        stock = stock.toIntOrNull() ?: 0,
                        esOrganico = esOrganico
                    )
                    viewModel.guardarProducto(producto)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = productoState !is UIState.Loading &&
                        nombre.isNotBlank() &&
                        descripcion.isNotBlank() &&
                        precio.isNotBlank() &&
                        stock.isNotBlank()
            ) {
                if (productoState is UIState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Guardar Producto")
                }
            }
        }
    }
}
