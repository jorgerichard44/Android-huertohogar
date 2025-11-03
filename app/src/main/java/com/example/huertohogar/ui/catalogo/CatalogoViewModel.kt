package com.example.huertohogar.ui.catalogo


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.huertohogar.data.model.Producto
import com.example.huertohogar.data.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CatalogoViewModel(private val repository: ProductoRepository) : ViewModel() {

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    private val _categoriaSeleccionada = MutableStateFlow("Todos")
    val categoriaSeleccionada: StateFlow<String> = _categoriaSeleccionada.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        cargarProductos()
    }

    private fun cargarProductos() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.inicializarDatosIniciales()
            repository.productos.collect { listaProductos ->
                _productos.value = listaProductos
                _isLoading.value = false
            }
        }
    }

    fun filtrarPorCategoria(categoria: String) {
        _categoriaSeleccionada.value = categoria
        viewModelScope.launch {
            _isLoading.value = true
            repository.filtrarPorCategoria(categoria).collect { productos ->
                _productos.value = productos
                _isLoading.value = false
            }
        }
    }

    fun buscarProductos(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            if (query.isBlank()) {
                repository.productos.collect {
                    _productos.value = it
                    _isLoading.value = false
                }
            } else {
                repository.buscarProductos(query).collect { productos ->
                    _productos.value = productos
                    _isLoading.value = false
                }
            }
        }
    }

    suspend fun obtenerProductoPorId(id: Int): Producto? {
        return repository.obtenerProductoPorId(id)
    }

    fun insertarProducto(producto: Producto) {
        viewModelScope.launch {
            repository.insertarProducto(producto)
        }
    }

    fun actualizarProducto(producto: Producto) {
        viewModelScope.launch {
            repository.actualizarProducto(producto)
        }
    }

    fun eliminarProducto(producto: Producto) {
        viewModelScope.launch {
            repository.eliminarProducto(producto)
        }
    }
}

class CatalogoViewModelFactory(
    private val repository: ProductoRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CatalogoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CatalogoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
