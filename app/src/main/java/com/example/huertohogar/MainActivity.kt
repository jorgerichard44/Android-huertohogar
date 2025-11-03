package com.example.huertohogar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.huertohogar.data.AppDatabase
import com.example.huertohogar.data.repository.ProductoRepository
import com.example.huertohogar.data.repository.UsuarioRepository
import com.example.huertohogar.navigation.AppNavigation
import com.example.huertohogar.ui.theme.HuertohogarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Inicializar base de datos
        val database = AppDatabase.getDatabase(applicationContext)

        // ✅ Inicializar Repositories
        val productoRepository = ProductoRepository(database.productoDao())
        val usuarioRepository = UsuarioRepository(database.usuarioDao())

        setContent {
            HuertohogarTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        productoRepository = productoRepository,
                        usuarioRepository = usuarioRepository
                    )
                }
            }
        }
    }
}
