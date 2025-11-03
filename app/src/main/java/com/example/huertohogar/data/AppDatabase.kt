package com.example.huertohogar.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.huertohogar.data.dao.ProductoDao
import com.example.huertohogar.data.dao.UsuarioDao
import com.example.huertohogar.data.model.Producto
import com.example.huertohogar.data.model.Usuario

@Database(
    entities = [
        Producto::class,
        Usuario::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // ✅ DAOs abstractos
    abstract fun productoDao(): ProductoDao
    abstract fun usuarioDao(): UsuarioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // ✅ SINGLETON: Una sola instancia de la base de datos
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "huertohogar_database"
                )
                    .fallbackToDestructiveMigration() // ⚠️ Elimina datos en cambios de versión
                    .build()

                INSTANCE = instance
                instance
            }
        }

        // ✅ Método para limpiar la instancia (útil para testing)
        fun clearInstance() {
            INSTANCE = null
        }
    }
}
