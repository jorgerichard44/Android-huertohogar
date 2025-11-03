package com.example.huertohogar.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.huertohogar.data.dao.*
import com.example.huertohogar.data.model.*

@Database(
    entities = [
        Producto::class,
        Usuario::class,
        Pedido::class,
        DetallePedido::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productoDao(): ProductoDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun pedidoDao(): PedidoDao
    abstract fun detallePedidoDao(): DetallePedidoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "huerto_hogar_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
