package com.tenshin.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tenshin.app.navigation.NavItem
import com.tenshin.app.ui.icons.WFIcons
import com.tenshin.app.ui.theme.*

// ══════════════════════════════════════════
//  DrawerContent
//  → Contenido del ModalNavigationDrawer de Material3
//    Header TENSHIN + NavigationDrawerItem por sección
//    Footer con quote
// ══════════════════════════════════════════
@Composable
fun DrawerContent(
    items:         List<NavItem>,
    activeId:      String,
    onNavigate:    (String) -> Unit,
    modifier:      Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ColorSurface),
    ) {
        // ── Header ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(ColorAccentGlow, ColorSurface),
                    )
                )
                .padding(start = 20.dp, end = 20.dp, top = 44.dp, bottom = 20.dp)
                .drawBehind {
                    drawLine(
                        color = ColorBorder,
                        start = androidx.compose.ui.geometry.Offset(0f, size.height),
                        end   = androidx.compose.ui.geometry.Offset(size.width, size.height),
                        strokeWidth = 1.dp.toPx(),
                    )
                },
        ) {
            Column {
                Text(
                    text       = "TENSHIN",
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = ColorAccent,
                    letterSpacing = 3.sp,
                )
                Text(
                    text     = "Espectro Consejero v5.0",
                    fontSize = 11.sp,
                    color    = ColorTextMuted,
                    modifier = Modifier.padding(top = 2.dp),
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 10.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(ColorGreen, CircleShape),
                    )
                    Text(
                        text     = "Arsenal sincronizado · 15/03 14:53",
                        fontSize = 11.sp,
                        color    = ColorGreen,
                    )
                }
            }
        }

        // ── Navigation items ──
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 12.dp),
        ) {
            items.forEach { item ->
                val isActive = activeId == item.id
                NavigationDrawerItem(
                    label = {
                        Text(
                            text       = item.label,
                            fontSize   = 13.sp,
                            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                            color      = if (isActive) item.color else ColorText,
                        )
                    },
                    selected = isActive,
                    onClick  = { onNavigate(item.id) },
                    icon = {
                        Canvas(modifier = Modifier.size(22.dp)) {
                            val drawFn = when (item.icon) {
                                "home"       -> WFIcons::home
                                "precio"     -> WFIcons::precio
                                "inventario" -> WFIcons::inventario
                                "plan"       -> WFIcons::plan
                                "rivens"     -> WFIcons::rivens
                                "baro"       -> WFIcons::baro
                                "sesiones"   -> WFIcons::sesiones
                                else         -> WFIcons::ask
                            }
                            drawFn(this, if (isActive) item.color else ColorTextMuted)
                        }
                    },
                    modifier = Modifier.padding(horizontal = 4.dp),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor   = item.color.copy(alpha = 0.09f),
                        unselectedContainerColor = ColorSurface,
                        selectedIconColor        = item.color,
                        unselectedIconColor      = ColorTextMuted,
                        selectedTextColor        = item.color,
                        unselectedTextColor      = ColorText,
                        selectedBadgeColor       = item.color,
                    ),
                    shape = RoundedCornerShape(
                        topStart    = 0.dp, bottomStart = 0.dp,
                        topEnd      = 8.dp, bottomEnd   = 8.dp,
                    ),
                )
            }
        }

        // ── Footer ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawLine(ColorBorder, androidx.compose.ui.geometry.Offset(0f, 0f),
                        androidx.compose.ui.geometry.Offset(size.width, 0f), strokeWidth = 1.dp.toPx())
                }
                .padding(horizontal = 20.dp, vertical = 14.dp),
        ) {
            Text(
                text      = "\"El Vacío recompensa la paciencia y castiga la avaricia.\"",
                fontSize  = 10.sp,
                color     = ColorTextDim,
                lineHeight = 14.sp,
            )
        }
    }
}
