package com.example.huertohogar.data.dao

import androidx.room.*
import com.example.huertohogar.data.model.Producto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    fun obtenerTodosLosProductos(): Flow<List<Producto>>

    @Query("SELECT * FROM productos WHERE categoria = :categoria ORDER BY nombre ASC")
    fun obtenerProductosPorCategoria(categoria: String): Flow<List<Producto>>

    @Query("SELECT * FROM productos WHERE nombre LIKE '%' || :query || '%' OR descripcion LIKE '%' || :query || '%'")
    fun buscarProductos(query: String): Flow<List<Producto>>

    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun obtenerProductoPorId(id: Int): Producto?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarProducto(producto: Producto): Long

    @Update
    suspend fun actualizarProducto(producto: Producto)

    @Delete
    suspend fun eliminarProducto(producto: Producto)

    @Query("UPDATE productos SET stock = stock - :cantidad WHERE id = :productoId")
    suspend fun reducirStock(productoId: Int, cantidad: Int)
}
