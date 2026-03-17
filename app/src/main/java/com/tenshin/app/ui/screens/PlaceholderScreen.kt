package com.tenshin.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tenshin.app.navigation.NavItem
import com.tenshin.app.ui.icons.WFIcons
import com.tenshin.app.ui.theme.*

// ══════════════════════════════════════════
//  PlaceholderScreen
//  → Para secciones aún no implementadas
//    Ícono grande + label + summary + badge "EN CONSTRUCCIÓN"
// ══════════════════════════════════════════
@Composable
fun PlaceholderScreen(
    item:     NavItem,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Ícono Canvas 64×64
            Canvas(modifier = Modifier.size(64.dp)) {
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
                drawFn(this, item.color)
            }

            Text(
                text       = item.label,
                fontSize   = 16.sp,
                fontWeight = FontWeight.Bold,
                color      = item.color,
                textAlign  = TextAlign.Center,
            )

            Text(
                text      = item.summary,
                fontSize  = 12.sp,
                color     = ColorTextMuted,
                textAlign = TextAlign.Center,
                lineHeight = 19.2.sp,
                modifier  = Modifier.widthIn(max = 260.dp),
            )

            // Badge "EN CONSTRUCCIÓN"
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(item.color.copy(alpha = 0.09f), RoundedCornerShape(10.dp))
                    .drawBehind {
                        drawRoundRect(
                            color        = item.color.copy(alpha = 0.27f),
                            cornerRadius = CornerRadius(10.dp.toPx()),
                            style        = Stroke(width = 1.dp.toPx()),
                        )
                    }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
            ) {
                Text(
                    text          = "EN CONSTRUCCIÓN",
                    fontSize      = 10.sp,
                    color         = ColorTextDim,
                    letterSpacing = 1.sp,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text     = "Pantalla disponible en la próxima versión",
                    fontSize = 11.sp,
                    color    = item.color,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
