package com.example.huertohogar.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.huertohogar.data.model.Usuario

@Composable
fun DrawerContent(
    usuarioActual: Usuario?,
    onNavigateToHome: () -> Unit,
    onNavigateToCatalogo: () -> Unit,
    onNavigateToCarrito: () -> Unit,
    onNavigateToPerfil: () -> Unit,
    onNavigateToPedidos: () -> Unit,
    onNavigateToAgregarProducto: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header del drawer
        usuarioActual?.let { usuario ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = usuario.nombre,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = usuario.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        HorizontalDivider()

        Spacer(modifier = Modifier.height(16.dp))

        // Opciones del menú
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Inicio") },
            selected = false,
            onClick = onNavigateToHome
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Store, contentDescription = null) },
            label = { Text("Catálogo") },
            selected = false,
            onClick = onNavigateToCatalogo
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
            label = { Text("Carrito") },
            selected = false,
            onClick = onNavigateToCarrito
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Receipt, contentDescription = null) },
            label = { Text("Mis Pedidos") },
            selected = false,
            onClick = onNavigateToPedidos
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("Mi Perfil") },
            selected = false,
            onClick = onNavigateToPerfil
        )

        if (usuarioActual?.esAdmin == true) {
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "Administración",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                label = { Text("Agregar Producto") },
                selected = false,
                onClick = onNavigateToAgregarProducto
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(8.dp))

        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Logout, contentDescription = null) },
            label = { Text("Cerrar Sesión") },
            selected = false,
            onClick = onLogout,
            colors = NavigationDrawerItemDefaults.colors(
                unselectedIconColor = MaterialTheme.colorScheme.error,
                unselectedTextColor = MaterialTheme.colorScheme.error
            )
        )
    }
}
