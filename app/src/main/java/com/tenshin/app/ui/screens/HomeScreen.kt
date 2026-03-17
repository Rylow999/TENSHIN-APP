package com.tenshin.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tenshin.app.navigation.NavItem
import com.tenshin.app.ui.components.PortalCard
import com.tenshin.app.ui.theme.*
import com.tenshin.app.ui.viewmodel.HomeViewModel
import com.tenshin.app.ui.viewmodel.UiState

@Composable
fun HomeScreen(
    portals:    List<NavItem>,
    onNavigate: (String) -> Unit,
    onBack:     () -> Unit,
    viewModel:  HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isHacked by viewModel.isHacked.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── Barra de Navegación Optimizada ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = MaterialTheme.colorScheme.primary)
            }
            
            Text(
                text = if (isHacked) "SYS_HOME" else "CENTRAL DE MANDO",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                letterSpacing = 2.sp,
                fontFamily = if (isHacked) FontFamily.Monospace else FontFamily.Default
            )

            IconButton(onClick = { viewModel.syncInventory() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refrescar", tint = MaterialTheme.colorScheme.primary)
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // ── Status card con Glow Dinámico ──
            item {
                StatusCard(uiState, isHacked)
            }

            // ── Quote del espectro ──
            item {
                Text(
                    text      = if (isHacked) "[DATA_CORRUPTED]: El Vacío nos reclama." else "\"El Vacío guarda sus secretos. Yo guardo los tuyos, Tenno.\"",
                    fontSize  = 12.sp,
                    color     = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    fontFamily = if (isHacked) FontFamily.Monospace else FontFamily.Default,
                    modifier  = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                )
            }

            // ── Portales ──
            item {
                Text(
                    text          = if (isHacked) "> ACCESO_A_RED" else "PORTALES DEL RELÉ",
                    fontSize      = 10.sp,
                    color         = MaterialTheme.colorScheme.primary,
                    fontWeight    = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    modifier      = Modifier.padding(bottom = 8.dp),
                )
            }

            val rows = portals.chunked(2)
            items(rows) { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    row.forEach { portal ->
                        PortalCard(
                            item    = portal,
                            onClick = { onNavigate(portal.id) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    if (row.size == 1) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun StatusCard(uiState: UiState, isHacked: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val accentColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .drawBehind {
                drawRoundRect(
                    color = accentColor.copy(alpha = 0.2f),
                    style = Stroke(width = 1.dp.toPx()),
                    cornerRadius = CornerRadius(16.dp.toPx())
                )
                // Glow effect
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(accentColor.copy(alpha = glowAlpha), Color.Transparent),
                        center = androidx.compose.ui.geometry.Offset(size.width * 0.9f, 0f),
                        radius = 120.dp.toPx()
                    ),
                    radius = 120.dp.toPx(),
                    center = androidx.compose.ui.geometry.Offset(size.width * 0.9f, 0f)
                )
            }
            .padding(20.dp),
    ) {
        Column {
            Text(
                text = if (isHacked) "CRITICAL_SYSTEM_SYNC" else "ARSENAL SINCRONIZADO",
                fontSize = 10.sp,
                color = accentColor,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                fontFamily = if (isHacked) FontFamily.Monospace else FontFamily.Default
            )
            
            Spacer(Modifier.height(12.dp))
            
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column {
                    Text(
                        text = if (isHacked) "0x7E0_P" else "~228p",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = accentColor,
                        fontFamily = FontFamily.Monospace,
                    )
                    Text(
                        text = "≈ $3 USD · 15/03 14:53",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(if (isHacked) "ERR_ITEMS" else "29 ítems WFM", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Text("16 Rivens · 14 Arcanos", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusChip("Sync OK", ColorGreen, isHacked)
                StatusChip("Baro: 5d", ColorGold, isHacked)
                StatusChip("Market: 3+", accentColor, isHacked)
            }
        }
    }
}

@Composable
fun StatusChip(text: String, color: Color, isHacked: Boolean) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Text(
            text = if (isHacked) "[${text.uppercase()}]" else text,
            fontSize = 9.sp,
            color = color,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontFamily = if (isHacked) FontFamily.Monospace else FontFamily.Default
        )
    }
}
