package com.example.huertohogar.data.dao



import androidx.room.*
import com.example.huertohogar.data.model.Producto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    // ✅ INSERTAR un producto
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarProducto(producto: Producto): Long

    // ✅ INSERTAR múltiples productos
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarProductos(productos: List<Producto>)

    // ✅ ACTUALIZAR un producto
    @Update
    suspend fun actualizarProducto(producto: Producto)

    // ✅ ELIMINAR un producto
    @Delete
    suspend fun eliminarProducto(producto: Producto)

    // ✅ OBTENER todos los productos (Flow para actualizaciones en tiempo real)
    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    fun obtenerTodosLosProductos(): Flow<List<Producto>>

    // ✅ OBTENER producto por ID
    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun obtenerProductoPorId(id: Int): Producto?

    // ✅ BUSCAR productos por nombre
    @Query("SELECT * FROM productos WHERE nombre LIKE '%' || :nombre || '%'")
    fun buscarProductosPorNombre(nombre: String): Flow<List<Producto>>

    // ✅ FILTRAR productos por categoría
    @Query("SELECT * FROM productos WHERE categoria = :categoria")
    fun obtenerProductosPorCategoria(categoria: String): Flow<List<Producto>>

    // ✅ OBTENER productos disponibles
    @Query("SELECT * FROM productos WHERE disponible = 1 AND stock > 0")
    fun obtenerProductosDisponibles(): Flow<List<Producto>>

    // ✅ ACTUALIZAR stock
    @Query("UPDATE productos SET stock = :nuevoStock WHERE id = :productoId")
    suspend fun actualizarStock(productoId: Int, nuevoStock: Int)

    // ✅ ELIMINAR todos los productos
    @Query("DELETE FROM productos")
    suspend fun eliminarTodosLosProductos()
}
