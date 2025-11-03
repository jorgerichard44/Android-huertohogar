package com.example.huertohogar.navigation


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.huertohogar.data.model.Usuario

@Composable
fun DrawerContent(
    usuarioActual: Usuario?,
    onNavigateToHome: () -> Unit,
    onNavigateToCatalogo: () -> Unit,
    onNavigateToCarrito: () -> Unit,
    onNavigateToPerfil: () -> Unit,
    onNavigateToAgregarProducto: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        // ✅ Header del Drawer
        DrawerHeader(usuario = usuarioActual)

        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        Spacer(modifier = Modifier.height(8.dp))

        // ✅ Opciones del menú
        DrawerMenuItem(
            icon = Icons.Default.Home,
            title = "Inicio",
            onClick = onNavigateToHome
        )

        DrawerMenuItem(
            icon = Icons.Default.ShoppingBag,
            title = "Catálogo",
            onClick = onNavigateToCatalogo
        )

        DrawerMenuItem(
            icon = Icons.Default.ShoppingCart,
            title = "Carrito",
            onClick = onNavigateToCarrito
        )

        if (usuarioActual != null) {
            DrawerMenuItem(
                icon = Icons.Default.Person,
                title = "Mi Perfil",
                onClick = onNavigateToPerfil
            )
        }

        DrawerMenuItem(
            icon = Icons.Default.Add,
            title = "Agregar Producto",
            onClick = onNavigateToAgregarProducto
        )

        Spacer(modifier = Modifier.weight(1f))

        Divider()

        if (usuarioActual != null) {
            DrawerMenuItem(
                icon = Icons.Default.ExitToApp,
                title = "Cerrar Sesión",
                onClick = onLogout
            )
        }
    }
}

@Composable
fun DrawerHeader(usuario: Usuario?) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Usuario",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (usuario != null) {
            Text(
                text = "${usuario.nombre} ${usuario.apellido}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = usuario.email,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Text(
                text = "HuertoHogar",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Del campo a tu hogar",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DrawerMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
