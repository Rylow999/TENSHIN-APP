package com.tenshin.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tenshin.app.navigation.NavItem
import com.tenshin.app.ui.icons.WFIcons
import com.tenshin.app.ui.theme.*

// ══════════════════════════════════════════
//  PortalCard
//  → var expanded by remember { mutableStateOf(false) }
//    background animado con animateColorAsState()
//    border animado  con animateColorAsState()
//    Modifier.animateContentSize(spring()) para la expansión
//    Box con Modifier.clickable { expanded = !expanded }
//    Ícono Canvas + label + stats preview / summary expandido
// ══════════════════════════════════════════
@Composable
fun PortalCard(
    item:      NavItem,
    onClick:   () -> Unit,
    modifier:  Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    // animateColorAsState — equivalente a "transition: background/border 0.25s"
    val bgColor by animateColorAsState(
        targetValue   = if (expanded) item.color.copy(alpha = 0.09f) else ColorSurface,
        animationSpec = spring(),
        label         = "portalCard_bg",
    )
    val borderColor by animateColorAsState(
        targetValue   = if (expanded) item.color.copy(alpha = 0.53f) else ColorBorder,
        animationSpec = spring(),
        label         = "portalCard_border",
    )
    val iconTint by animateColorAsState(
        targetValue   = if (expanded) item.color else ColorTextMuted,
        animationSpec = spring(),
        label         = "portalCard_icon",
    )
    val labelColor by animateColorAsState(
        targetValue   = if (expanded) item.color else ColorText,
        animationSpec = spring(),
        label         = "portalCard_label",
    )
    val glowAlpha by animateColorAsState(
        targetValue = if (expanded) item.color.copy(alpha = 0.16f) else Color.Transparent,
        animationSpec = spring(),
        label = "portalCard_glow",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .drawBehind {
                // Borde animado
                drawRoundRect(
                    color        = borderColor,
                    cornerRadius = CornerRadius(16.dp.toPx()),
                    style        = Stroke(width = 1.dp.toPx()),
                )
                // Glow top-right
                drawCircle(
                    brush  = Brush.radialGradient(
                        colors = listOf(glowAlpha, Color.Transparent),
                        center = Offset(size.width, 0f),
                        radius = size.width * 0.5f,
                    ),
                    radius = size.width * 0.5f,
                    center = Offset(size.width, 0f),
                )
                // Línea de acento inferior
                if (expanded) {
                    drawLine(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color.Transparent, item.color.copy(alpha = 0.4f), Color.Transparent),
                        ),
                        start = Offset(0f, size.height - 1.dp.toPx()),
                        end   = Offset(size.width, size.height - 1.dp.toPx()),
                        strokeWidth = 2.dp.toPx(),
                    )
                }
            }
            .background(bgColor)
            .clickable {
                expanded = !expanded
                onClick()
            }
            .padding(14.dp),
    ) {
        Column {
            // ── Ícono + label ──
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                // Canvas del ícono 36×36
                Canvas(modifier = Modifier.size(36.dp)) {
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
                    drawFn(this, iconTint)
                }

                Column {
                    Text(
                        text       = item.label,
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = labelColor,
                    )
                    // Stats preview — visible sólo cuando no está expandido
                    if (!expanded && item.stats != null) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            item.stats.filter { it.value.isNotBlank() }.forEach { stat ->
                                Text(
                                    text     = stat.value,
                                    fontSize = 10.sp,
                                    color    = item.color.copy(alpha = 0.8f),
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                )
                            }
                        }
                    }
                }
            }

            // ── Descripción expandida + stat chips ──
            if (expanded) {
                Spacer(Modifier.height(10.dp))
                Text(
                    text       = item.summary,
                    fontSize   = 11.sp,
                    color      = ColorTextMuted,
                    lineHeight = 17.6.sp,
                )
                item.stats?.let { stats ->
                    val meaningful = stats.filter { it.value.isNotBlank() }
                    if (meaningful.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            meaningful.forEach { stat ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment     = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .background(item.color.copy(alpha = 0.09f), RoundedCornerShape(6.dp))
                                        .drawBehind {
                                            drawRoundRect(
                                                color        = item.color.copy(alpha = 0.27f),
                                                cornerRadius = CornerRadius(6.dp.toPx()),
                                                style        = Stroke(width = 1.dp.toPx()),
                                            )
                                        }
                                        .padding(horizontal = 8.dp, vertical = 3.dp),
                                ) {
                                    Text(stat.label, fontSize = 9.sp, color = ColorTextMuted,
                                        letterSpacing = 0.5.sp)
                                    Text(stat.value, fontSize = 11.sp, color = item.color,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
