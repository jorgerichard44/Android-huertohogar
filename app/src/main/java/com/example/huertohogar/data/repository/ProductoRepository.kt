package com.example.huertohogar.data.repository

import com.example.huertohogar.data.dao.ProductoDao
import com.example.huertohogar.data.model.Producto
import kotlinx.coroutines.flow.Flow

class ProductoRepository(private val productoDao: ProductoDao) {

    fun obtenerTodosLosProductos(): Flow<List<Producto>> {
        return productoDao.obtenerTodosLosProductos()
    }

    fun obtenerProductosPorCategoria(categoria: String): Flow<List<Producto>> {
        return productoDao.obtenerProductosPorCategoria(categoria)
    }

    fun buscarProductos(query: String): Flow<List<Producto>> {
        return productoDao.buscarProductos(query)
    }

    suspend fun obtenerProductoPorId(id: Int): Producto? {
        return productoDao.obtenerProductoPorId(id)
    }

    suspend fun insertarProducto(producto: Producto): Long {
        return productoDao.insertarProducto(producto)
    }

    suspend fun actualizarProducto(producto: Producto) {
        productoDao.actualizarProducto(producto)
    }

    suspend fun eliminarProducto(producto: Producto) {
        productoDao.eliminarProducto(producto)
    }

    suspend fun reducirStock(productoId: Int, cantidad: Int) {
        productoDao.reducirStock(productoId, cantidad)
    }

    // Datos iniciales
    suspend fun inicializarDatosIniciales() {
        val productosIniciales = listOf(
            Producto(nombre = "Lechuga", descripcion = "Lechuga fresca orgánica", precio = 1500.0, categoria = "Verduras", stock = 50),
            Producto(nombre = "Tomate", descripcion = "Tomates rojos maduros", precio = 2000.0, categoria = "Verduras", stock = 40),
            Producto(nombre = "Zanahoria", descripcion = "Zanahorias frescas", precio = 1200.0, categoria = "Verduras", stock = 60),
            Producto(nombre = "Manzana", descripcion = "Manzanas rojas dulces", precio = 2500.0, categoria = "Frutas", stock = 30),
            Producto(nombre = "Naranja", descripcion = "Naranjas jugosas", precio = 1800.0, categoria = "Frutas", stock = 45),
            Producto(nombre = "Albahaca", descripcion = "Albahaca fresca aromática", precio = 800.0, categoria = "Hierbas", stock = 25),
            Producto(nombre = "Cilantro", descripcion = "Cilantro fresco", precio = 600.0, categoria = "Hierbas", stock = 35),
            Producto(nombre = "Perejil", descripcion = "Perejil verde fresco", precio = 500.0, categoria = "Hierbas", stock = 40)
        )

        productosIniciales.forEach { producto ->
            insertarProducto(producto)
        }
    }
}
