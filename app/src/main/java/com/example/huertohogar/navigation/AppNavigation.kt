package com.example.huertohogar.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.huertohogar.data.repository.ProductoRepository
import com.example.huertohogar.data.repository.UsuarioRepository
import com.example.huertohogar.ui.carrito.CarritoScreen
import com.example.huertohogar.ui.carrito.CarritoViewModel
import com.example.huertohogar.ui.catalogo.CatalogoScreen
import com.example.huertohogar.ui.catalogo.CatalogoViewModel
import com.example.huertohogar.ui.catalogo.CatalogoViewModelFactory
import com.example.huertohogar.ui.catalogo.ProductoFormScreen
import com.example.huertohogar.ui.login.LoginScreen
import com.example.huertohogar.ui.login.LoginViewModel
import com.example.huertohogar.ui.login.LoginViewModelFactory
import com.example.huertohogar.ui.perfil.PerfilScreen
import com.example.huertohogar.ui.perfil.PerfilViewModel
import com.example.huertohogar.ui.perfil.PerfilViewModelFactory
import com.example.huertohogar.ui.registro.RegistroScreen
import com.example.huertohogar.ui.registro.RegistroViewModel
import com.example.huertohogar.ui.registro.RegistroViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    productoRepository: ProductoRepository,
    usuarioRepository: UsuarioRepository
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // ViewModels compartidos
    val catalogoViewModel: CatalogoViewModel = viewModel(
        factory = CatalogoViewModelFactory(productoRepository)
    )

    val loginViewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(usuarioRepository)
    )

    val carritoViewModel: CarritoViewModel = viewModel()

    // Estado del usuario actual
    val usuarioActual by loginViewModel.usuarioActual.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(
                    usuarioActual = usuarioActual,
                    onNavigateToHome = {
                        navController.navigate(Routes.Catalogo.route) {
                            popUpTo(Routes.Catalogo.route) { inclusive = true }
                        }
                        scope.launch { drawerState.close() }
                    },
                    onNavigateToCatalogo = {
                        navController.navigate(Routes.Catalogo.route)
                        scope.launch { drawerState.close() }
                    },
                    onNavigateToCarrito = {
                        navController.navigate(Routes.Carrito.route)
                        scope.launch { drawerState.close() }
                    },
                    onNavigateToPerfil = {
                        usuarioActual?.let {
                            navController.navigate(Routes.Perfil.createRoute(it.id))
                            scope.launch { drawerState.close() }
                        }
                    },
                    onNavigateToAgregarProducto = {
                        navController.navigate(Routes.ProductoForm.route)
                        scope.launch { drawerState.close() }
                    },
                    onLogout = {
                        loginViewModel.logout()
                        navController.navigate(Routes.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = if (usuarioActual != null) Routes.Catalogo.route else Routes.Login.route
        ) {
            // ✅ LOGIN
            composable(Routes.Login.route) {
                LoginScreen(
                    viewModel = loginViewModel,
                    onNavigateToRegistro = {
                        navController.navigate(Routes.Registro.route)
                    },
                    onLoginSuccess = {
                        navController.navigate(Routes.Catalogo.route) {
                            popUpTo(Routes.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            // ✅ REGISTRO
            composable(Routes.Registro.route) {
                val registroViewModel: RegistroViewModel = viewModel(
                    factory = RegistroViewModelFactory(usuarioRepository)
                )

                RegistroScreen(
                    viewModel = registroViewModel,
                    onNavigateToLogin = {
                        navController.popBackStack()
                    },
                    onRegistroExitoso = {
                        navController.navigate(Routes.Login.route) {
                            popUpTo(Routes.Registro.route) { inclusive = true }
                        }
                    }
                )
            }

            // ✅ CATÁLOGO (HOME)
            composable(Routes.Catalogo.route) {
                CatalogoScreen(
                    viewModel = catalogoViewModel,
                    carritoViewModel = carritoViewModel,
                    onNavigateToProductoForm = { productoId ->
                        if (productoId != null) {
                            navController.navigate(Routes.ProductoForm.createRoute(productoId))
                        } else {
                            navController.navigate(Routes.ProductoForm.route)
                        }
                    },
                    onOpenDrawer = {
                        scope.launch { drawerState.open() }
                    }
                )
            }

            // ✅ FORMULARIO DE PRODUCTO (Agregar/Editar)
            composable(
                route = Routes.ProductoForm.routeWithArgs,
                arguments = listOf(
                    navArgument("productoId") {
                        type = NavType.IntType
                        defaultValue = -1
                    }
                )
            ) { backStackEntry ->
                val productoId = backStackEntry.arguments?.getInt("productoId")

                ProductoFormScreen(
                    viewModel = catalogoViewModel,
                    productoId = if (productoId == -1) null else productoId,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            // ✅ CARRITO
            composable(Routes.Carrito.route) {
                CarritoScreen(
                    viewModel = carritoViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToCatalogo = {
                        navController.navigate(Routes.Catalogo.route) {
                            popUpTo(Routes.Catalogo.route) { inclusive = true }
                        }
                    }
                )
            }

            // ✅ PERFIL - AQUÍ ESTABA INCOMPLETO
            composable(
                route = Routes.Perfil.routeWithArgs,
                arguments = listOf(
                    navArgument("usuarioId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->
                val usuarioId = backStackEntry.arguments?.getInt("usuarioId") ?: 0

                val perfilViewModel: PerfilViewModel = viewModel(
                    factory = PerfilViewModelFactory(usuarioRepository)
                )

                PerfilScreen(
                    viewModel = perfilViewModel,
                    usuarioId = usuarioId,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
