package com.example.huertohogar.data.repository



import com.example.huertohogar.data.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProductoRepository {
    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    init {
        cargarProductos()
    }

    private fun cargarProductos() {
        _productos.value = listOf(
            Producto(
                id = "1",
                nombre = "Tomates Orgánicos",
                descripcion = "Tomates frescos cultivados sin pesticidas",
                precio = 2500.0,
                categoria = "Verduras",
                origen = "Región Metropolitana",
                disponible = true,
                imagenUrl = "",
                stock = 50
            ),
            Producto(
                id = "2",
                nombre = "Lechugas Hidropónicas",
                descripcion = "Lechugas frescas cultivadas en sistema hidropónico",
                precio = 1800.0,
                categoria = "Verduras",
                origen = "Valparaíso",
                disponible = true,
                imagenUrl = "",
                stock = 30
            ),
            Producto(
                id = "3",
                nombre = "Manzanas Fuji",
                descripcion = "Manzanas dulces y crujientes",
                precio = 3200.0,
                categoria = "Frutas",
                origen = "Región del Maule",
                disponible = true,
                imagenUrl = "",
                stock = 100
            ),
            Producto(
                id = "4",
                nombre = "Zanahorias Orgánicas",
                descripcion = "Zanahorias frescas sin químicos",
                precio = 1500.0,
                categoria = "Verduras",
                origen = "Concepción",
                disponible = true,
                imagenUrl = "",
                stock = 75
            ),
            Producto(
                id = "5",
                nombre = "Fresas Premium",
                descripcion = "Fresas dulces de temporada",
                precio = 4500.0,
                categoria = "Frutas",
                origen = "Puerto Montt",
                disponible = true,
                imagenUrl = "",
                stock = 25
            ),
            Producto(
                id = "6",
                nombre = "Papas Nativas",
                descripcion = "Papas chilenas de variedades ancestrales",
                precio = 2000.0,
                categoria = "Verduras",
                origen = "Chiloé",
                disponible = true,
                imagenUrl = "",
                stock = 60
            )
        )
    }

    fun obtenerProductoPorId(id: String): Producto? {
        return _productos.value.find { it.id == id }
    }

    fun filtrarPorCategoria(categoria: String): List<Producto> {
        return if (categoria == "Todos") {
            _productos.value
        } else {
            _productos.value.filter { it.categoria == categoria }
        }
    }

    fun buscarProductos(query: String): List<Producto> {
        return _productos.value.filter {
            it.nombre.contains(query, ignoreCase = true) ||
                    it.descripcion.contains(query, ignoreCase = true)
        }
    }
}
