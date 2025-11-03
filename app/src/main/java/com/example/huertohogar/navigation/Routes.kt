package com.example.huertohogar.navigation

sealed class Routes(val route: String) {
    object Login : Routes("login")
    object Registro : Routes("registro")
    object Catalogo : Routes("catalogo")
    object Carrito : Routes("carrito")
    object Pedidos : Routes("pedidos")

    object ProductoForm : Routes("producto_form") {
        const val routeWithArgs = "producto_form?productoId={productoId}"
        fun createRoute(productoId: Int?) = if (productoId != null) {
            "producto_form?productoId=$productoId"
        } else {
            "producto_form"
        }
    }

    object Perfil : Routes("perfil") {
        const val routeWithArgs = "perfil/{usuarioId}"
        fun createRoute(usuarioId: Int) = "perfil/$usuarioId"
    }
}
