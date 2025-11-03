package com.example.huertohogar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.huertohogar.data.AppDatabase
import com.example.huertohogar.data.repository.PedidoRepository
import com.example.huertohogar.data.repository.ProductoRepository
import com.example.huertohogar.data.repository.UsuarioRepository
import com.example.huertohogar.navigation.AppNavigation
import com.example.huertohogar.ui.theme.HuertohogarTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)

        val productoRepository = ProductoRepository(database.productoDao())
        val usuarioRepository = UsuarioRepository(database.usuarioDao())
        val pedidoRepository = PedidoRepository(
            database.pedidoDao(),
            database.detallePedidoDao(),
            database.productoDao()
        )

        lifecycleScope.launch {
            productoRepository.inicializarDatosIniciales()
        }

        setContent {
            HuertohogarTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        productoRepository = productoRepository,
                        usuarioRepository = usuarioRepository,
                        pedidoRepository = pedidoRepository
                    )
                }
            }
        }
    }
}
