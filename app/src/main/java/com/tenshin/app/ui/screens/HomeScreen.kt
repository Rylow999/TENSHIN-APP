package com.tenshin.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.tenshin.app.ui.viewmodel.InventoryUiState
import com.tenshin.app.ui.viewmodel.InventoryViewModel

@Composable
fun HomeScreen(
    portals:    List<NavItem>,
    onNavigate: (String) -> Unit,
    inventoryViewModel: InventoryViewModel = viewModel()
) {
    val inventoryState by inventoryViewModel.uiState.collectAsState()
    val isHacked by inventoryViewModel.isHacked.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.size(48.dp))
            
            Text(
                text = if (isHacked) "HÖLLVANIA_HOME" else "COMANDO CENTRAL",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                letterSpacing = 2.sp,
                fontFamily = if (isHacked) FontFamily.Monospace else FontFamily.Default
            )

            IconButton(onClick = { inventoryViewModel.syncInventory() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refrescar", tint = MaterialTheme.colorScheme.primary)
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                StatusCard(inventoryState, isHacked)
            }

            item {
                Text(
                    text      = if (isHacked) "[DATA_INTACTA]: El Vacío nos observa. No hay vuelta atrás." else "El Vacío recompensa la paciencia y castiga la avaricia.",
                    fontSize  = 12.sp,
                    color     = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    fontFamily = if (isHacked) FontFamily.Monospace else FontFamily.Default,
                    modifier  = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                )
            }

            item {
                Text(
                    text          = if (isHacked) "ACCESO_RED" else "PORTALES DISPONIBLES",
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
fun StatusCard(inventoryState: InventoryUiState, isHacked: Boolean) {
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
    var showDetails by remember { mutableStateOf(false) }

    val inventory = (inventoryState as? InventoryUiState.Success)?.inventory
    val totalPlat = inventory?.items?.sumOf { (it.avgPrice ?: 0.0) * it.quantity } ?: 0.0
    val itemCount = inventory?.items?.size ?: 0

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { showDetails = !showDetails }
            .drawBehind {
                drawRoundRect(
                    color = accentColor.copy(alpha = 0.2f),
                    style = Stroke(width = 1.dp.toPx()),
                    cornerRadius = CornerRadius(16.dp.toPx())
                )
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
                text = if (inventory != null) {
                    if (isHacked) "CRITICAL_SYSTEM_SYNC" else "ARSENAL SINCRONIZADO"
                } else {
                    "SINCRONIZACIÓN REQUERIDA"
                },
                fontSize = 10.sp,
                color = if (inventory != null) accentColor else Color.Gray,
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
                        text = if (inventory != null) "~${totalPlat.toInt()}p" else "???",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = accentColor,
                        fontFamily = FontFamily.Monospace,
                    )
                    Text(
                        text = if (inventory != null) "≈ \$${(totalPlat * 0.013).toInt()} USD" else "Valor desconocido",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(if (inventory != null) "$itemCount ítems WFM" else "Ítems: ?", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            }

            if (showDetails && inventory != null) {
                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = accentColor.copy(alpha = 0.2f))
                inventory.items.take(5).forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(item.name, fontSize = 10.sp, color = Color.Gray)
                        Text("${(item.avgPrice ?: 0.0).toInt()}p", fontSize = 10.sp, color = accentColor)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusChip(if (inventory != null) "Sync OK" else "Offline", if (inventory != null) ColorGreen else Color.Gray, isHacked)
                StatusChip("Market: Conectado", accentColor, isHacked)
            }
        }
    }
}

@Composable
fun StatusChip(text: String, color: Color, isHacked: Boolean) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
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
