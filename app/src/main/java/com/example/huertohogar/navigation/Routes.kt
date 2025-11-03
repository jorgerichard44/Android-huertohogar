package com.example.huertohogar.navigation



sealed class Routes(val route: String) {

    // ✅ Autenticación
    object Login : Routes("login")
    object Registro : Routes("registro")

    // ✅ Principal
    object Catalogo : Routes("catalogo")

    // ✅ Productos
    object ProductoForm : Routes("producto_form") {
        const val routeWithArgs = "producto_form?productoId={productoId}"
        fun createRoute(productoId: Int) = "producto_form?productoId=$productoId"
    }

    // ✅ Carrito
    object Carrito : Routes("carrito")

    // ✅ Perfil
    object Perfil : Routes("perfil/{usuarioId}") {
        const val routeWithArgs = "perfil/{usuarioId}"
        fun createRoute(usuarioId: Int) = "perfil/$usuarioId"
    }
}
