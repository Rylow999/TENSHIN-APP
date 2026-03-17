package com.tenshin.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tenshin.app.ui.screens.*

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Home.route,
    portals: List<NavItem>,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                portals = portals,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Home.route)
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Screen.Precio.route) {
            PrecioScreen()
        }
        
        composable(Screen.Inventario.route) {
            InventarioScreen()
        }

        composable(Screen.Mundo.route) {
            WorldStateScreen()
        }

        composable(Screen.Soporte.route) {
            SoporteScreen()
        }

        composable(Screen.Plan.route) {
            PlanScreen()
        }

        composable(Screen.Baro.route) {
            BaroScreen()
        }
        
        // Placeholder routes for screens not yet fully implemented
        val placeholderRoutes = listOf(
            Screen.Rivens.route,
            Screen.Sesiones.route
        )
        
        placeholderRoutes.forEach { route ->
            composable(route) {
                val item = portals.find { it.id == route } ?: tenshinNavItems.first()
                PlaceholderScreen(item = item)
            }
        }
    }
}
