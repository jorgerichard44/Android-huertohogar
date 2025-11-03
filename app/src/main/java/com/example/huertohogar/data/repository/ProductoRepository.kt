package com.example.huertohogar.data.repository

import com.example.huertohogar.data.dao.ProductoDao
import com.example.huertohogar.data.model.Producto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class ProductoRepository(private val productoDao: ProductoDao) {

    // ✅ Flow de todos los productos (se actualiza automáticamente)
    val productos: Flow<List<Producto>> = productoDao.obtenerTodosLosProductos()

    // ✅ Inicializar datos de prueba si la BD está vacía
    suspend fun inicializarDatosIniciales() {
        if (productoDao.contarProductos() == 0) {
            val productosIniciales = listOf(
                Producto(
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

            productoDao.insertarProductos(productosIniciales)
        }
    }

    // ✅ Obtener producto por ID
    suspend fun obtenerProductoPorId(id: Int): Producto? {
        return productoDao.obtenerProductoPorId(id)
    }

    // ✅ Filtrar por categoría
    fun filtrarPorCategoria(categoria: String): Flow<List<Producto>> {
        return if (categoria == "Todos") {
            productoDao.obtenerTodosLosProductos()
        } else {
            productoDao.filtrarPorCategoria(categoria)
        }
    }

    // ✅ Buscar productos
    fun buscarProductos(query: String): Flow<List<Producto>> {
        return productoDao.buscarProductos(query)
    }

    // ✅ Insertar producto
    suspend fun insertarProducto(producto: Producto) {
        productoDao.insertarProducto(producto)
    }

    // ✅ Actualizar producto
    suspend fun actualizarProducto(producto: Producto) {
        productoDao.actualizarProducto(producto)
    }

    // ✅ Eliminar producto
    suspend fun eliminarProducto(producto: Producto) {
        productoDao.eliminarProducto(producto)
    }

    // ✅ Actualizar stock
    suspend fun actualizarStock(productoId: Int, nuevoStock: Int) {
        productoDao.actualizarStock(productoId, nuevoStock)
    }
}
