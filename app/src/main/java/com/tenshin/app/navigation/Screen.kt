package com.tenshin.app.navigation

import androidx.compose.ui.graphics.Color
import com.tenshin.app.ui.theme.ColorAccent
import com.tenshin.app.ui.theme.ColorGold
import com.tenshin.app.ui.theme.ColorGreen
import com.tenshin.app.ui.theme.ColorRiven

// ══════════════════════════════════════════
//  Rutas de navegación — sealed class Screen
// ══════════════════════════════════════════
sealed class Screen(val route: String) {
    object Sync        : Screen("sync")
    object Home        : Screen("home")
    object Precio      : Screen("precio")
    object Inventario  : Screen("inventario")
    object Plan        : Screen("plan")
    object Rivens      : Screen("rivens")
    object Baro        : Screen("baro")
    object Sesiones    : Screen("sesiones")
    object Sistema     : Screen("sistema")
    object Soporte     : Screen("soporte")
}


// ══════════════════════════════════════════
//  Modelo de datos de navegación del Drawer
// ══════════════════════════════════════════

data class NavStat(val label: String, val value: String)

data class NavItem(
    val id:      String,
    val icon:    String,
    val label:   String,
    val color:   Color,
    val summary: String,
    val stats:   List<NavStat>? = null,
)

val tenshinNavItems = listOf(
    NavItem(
        id = "home", icon = "home", label = "Inicio", color = ColorAccent,
        summary = "Panel principal con resumen del arsenal y estado del mercado.",
        stats = null,
    ),
    NavItem(
        id = "sistema", icon = "public", label = "Estado del Sistema", color = ColorAccent,
        summary = "Ciclos de día/noche, invasiones, fisuras y alertas activas en el Sistema Origen.",
        stats = listOf(NavStat("Cetus", "Día"), NavStat("Vallis", "Frío")),
    ),
    NavItem(
        id = "precio", icon = "precio", label = "Precio / Tendencia", color = ColorAccent,
        summary = "Consulta precios en tiempo real y el historial de 90 días de cualquier ítem en warframe.market.",
        stats = listOf(NavStat("Ítems WFM", "?"), NavStat("En alza", "?")),
    ),
    NavItem(
        id = "inventario", icon = "inventario", label = "Inventario", color = ColorGold,
        summary = "Estado del arsenal con precio × cantidad real. Valor total estimado del inventario sincronizado.",
        stats = listOf(NavStat("Arsenal", "Desconocido"), NavStat("? USD", "")),
    ),
    NavItem(
        id = "plan", icon = "plan", label = "Plan del día", color = ColorGreen,
        summary = "Directiva diaria personalizada: qué soltar, dónde farmear y movimientos del mercado a aprovechar.",
        stats = listOf(NavStat("Sync", "No")),
    ),
    NavItem(
        id = "rivens", icon = "rivens", label = "Rivens", color = ColorRiven,
        summary = "Análisis de Rivens con veredicto (soltar / refundir / guardar) y precio sugerido según disposición.",
        stats = listOf(NavStat("Rivens", "?"), NavStat("Velados", "?")),
    ),
    NavItem(
        id = "baro", icon = "baro", label = "Baro Ki'Teer", color = ColorGold,
        summary = "Estado del Comerciante del Vacío: ubicación, inventario completo y análisis de qué comprar.",
        stats = listOf(NavStat("Estado", "?"), NavStat("Llega en", "?")),
    ),
    NavItem(
        id = "sesiones", icon = "sesiones", label = "Sesiones", color = ColorGreen,
        summary = "Registro de operaciones con duración, actividad y Platinum estimado. Análisis de rendimiento.",
        stats = listOf(NavStat("Sesiones", "0"), NavStat("0h jugadas", "")),
    ),
    NavItem(
        id = "soporte", icon = "favorite", label = "Apoyar el Proyecto", color = ColorGold,
        summary = "Ayuda a mantener Tenshin libre y de código abierto. Donaciones y ventajas estéticas.",
        stats = listOf(NavStat("Estado", "Activo")),
    ),
)
