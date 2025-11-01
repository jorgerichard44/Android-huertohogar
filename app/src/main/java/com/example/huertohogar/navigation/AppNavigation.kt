package com.example.huertohogar.navigation



import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.huertohogar.ui.carrito.CarritoScreen
import com.example.huertohogar.ui.catalogo.CatalogoScreen
import com.example.huertohogar.ui.login.LoginScreen
import com.example.huertohogar.ui.perfil.PerfilScreen
import com.example.huertohogar.ui.registro.RegistroScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Registro : Screen("registro")
    object Catalogo : Screen("catalogo")
    object Carrito : Screen("carrito")
    object Perfil : Screen("perfil")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegistro = {
                    navController.navigate(Screen.Registro.route)
                },
                onNavigateToCatalogo = {
                    navController.navigate(Screen.Catalogo.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Registro.route) {
            RegistroScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegistroExitoso = {
                    navController.navigate(Screen.Catalogo.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Catalogo.route) {
            CatalogoScreen(
                onNavigateToCarrito = {
                    navController.navigate(Screen.Carrito.route)
                },
                onNavigateToPerfil = {
                    navController.navigate(Screen.Perfil.route)
                }
            )
        }

        composable(Screen.Carrito.route) {
            CarritoScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Perfil.route) {
            PerfilScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
