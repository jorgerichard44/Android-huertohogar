package com.example.huertohogar.ui.catalogo


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.huertohogar.data.model.Producto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoFormScreen(
    viewModel: CatalogoViewModel,
    productoId: Int? = null,
    onNavigateBack: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("Verduras") }
    var origen by remember { mutableStateOf("") }
    var imagenUrl by remember { mutableStateOf("") }
    var disponible by remember { mutableStateOf(true) }

    val categorias = listOf("Verduras", "Frutas", "Hortalizas", "Legumbres")
    var expandedCategoria by remember { mutableStateOf(false) }

    // Cargar datos si es edición
    LaunchedEffect(productoId) {
        productoId?.let { id ->
            viewModel.obtenerProductoPorId(id)?.let { producto ->
                nombre = producto.nombre
                descripcion = producto.descripcion
                precio = producto.precio.toString()
                stock = producto.stock.toString()
                categoria = producto.categoria
                origen = producto.origen
                imagenUrl = producto.imagenUrl
                disponible = producto.disponible
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (productoId == null) "Nuevo Producto" else "Editar Producto")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del Producto") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Descripción
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            // Precio
            OutlinedTextField(
                value = precio,
                onValueChange = { precio = it },
                label = { Text("Precio (CLP)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                prefix = { Text("$") }
            )

            // Stock
            OutlinedTextField(
                value = stock,
                onValueChange = { stock = it },
                label = { Text("Stock") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Categoría (Dropdown)
            ExposedDropdownMenuBox(
                expanded = expandedCategoria,
                onExpandedChange = { expandedCategoria = !expandedCategoria }
            ) {
                OutlinedTextField(
                    value = categoria,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expandedCategoria,
                    onDismissRequest = { expandedCategoria = false }
                ) {
                    categorias.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                categoria = cat
                                expandedCategoria = false
                            }
                        )
                    }
                }
            }

            // Origen
            OutlinedTextField(
                value = origen,
                onValueChange = { origen = it },
                label = { Text("Origen/Región") },
                modifier = Modifier.fillMaxWidth()
            )

            // URL de Imagen
            OutlinedTextField(
                value = imagenUrl,
                onValueChange = { imagenUrl = it },
                label = { Text("URL de Imagen (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )

            // Disponible (Switch)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Producto Disponible", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = disponible,
                    onCheckedChange = { disponible = it }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botón Guardar
            Button(
                onClick = {
                    val producto = Producto(
                        id = productoId ?: 0,
                        nombre = nombre,
                        descripcion = descripcion,
                        precio = precio.toDoubleOrNull() ?: 0.0,
                        stock = stock.toIntOrNull() ?: 0,
                        categoria = categoria,
                        origen = origen,
                        imagenUrl = imagenUrl,
                        disponible = disponible
                    )

                    if (productoId == null) {
                        viewModel.insertarProducto(producto)
                    } else {
                        viewModel.actualizarProducto(producto)
                    }

                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = nombre.isNotBlank() && precio.isNotBlank() && stock.isNotBlank()
            ) {
                Text(if (productoId == null) "Agregar Producto" else "Guardar Cambios")
            }
        }
    }
}
