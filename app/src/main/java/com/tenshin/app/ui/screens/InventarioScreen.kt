package com.tenshin.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tenshin.app.data.model.InventoryItem
import com.tenshin.app.data.model.ItemCategory
import com.tenshin.app.ui.theme.ColorAccent
import com.tenshin.app.ui.theme.ColorGold
import com.tenshin.app.ui.viewmodel.InventoryUiState
import com.tenshin.app.ui.viewmodel.InventoryViewModel
import com.tenshin.app.data.remote.NetworkDiscovery
import com.tenshin.app.di.NetworkModule
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun InventarioScreen(
    viewModel: InventoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isExpanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<ItemCategory?>(null) }
    var showInstructions by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "ARSENAL TENSHIN",
            style = MaterialTheme.typography.headlineSmall,
            color = ColorAccent,
            modifier = Modifier.padding(16.dp)
        )

        // PANEL DE INSTRUCCIONES
        if (showInstructions) {
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .border(BorderStroke(1.dp, ColorGold.copy(alpha = 0.5f)), RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = ColorGold.copy(alpha = 0.05f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = ColorGold)
                        Spacer(Modifier.width(8.dp))
                        Text("SINCRONIZACIÓN ACTIVA", fontWeight = FontWeight.Bold, color = ColorGold, fontSize = 12.sp)
                    }
                    Text(
                        "1. Abre Warframe en tu PC.\n2. Ejecuta warframe.helper.exe.\n3. Asegúrate de estar en la misma red WiFi.",
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                    )
                    Button(
                        onClick = { showInstructions = false },
                        modifier = Modifier.align(Alignment.End).height(30.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ColorGold.copy(alpha = 0.2f))
                    ) {
                        Text("ENTENDIDO", fontSize = 10.sp, color = ColorGold)
                    }
                }
            }
        }

        Button(
            onClick = {
                scope.launch {
                    val discovery = NetworkDiscovery(context)
                    val ip = discovery.discoverHelperIp()
                    if (ip != null) {
                        NetworkModule.setHelperIp(ip)
                        viewModel.syncInventory()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ColorAccent)
        ) {
            Text("SCAN & SYNC REAL-TIME")
        }

        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            when (val state = uiState) {
                is InventoryUiState.Success -> {
                    // Central "Lotus/Tenshin" Button
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(if (isExpanded) ColorGold else ColorAccent)
                            .clickable { isExpanded = !isExpanded },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isExpanded) "CERRAR" else "TENSHIN",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Radiating categories
                    ItemCategory.values().forEachIndexed { index, category ->
                        val angle = (index * (360f / ItemCategory.values().size)).toDouble()
                        val radius = if (isExpanded) 140.dp else 0.dp
                        val animatedRadius by animateDpAsState(
                            targetValue = radius,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                            label = "radius"
                        )

                        val x = (animatedRadius.value * cos(Math.toRadians(angle))).dp
                        val y = (animatedRadius.value * sin(Math.toRadians(angle))).dp

                        Column(
                            modifier = Modifier
                                .offset { IntOffset(x.roundToPx(), y.roundToPx()) }
                                .size(75.dp)
                                .clip(CircleShape)
                                .background(ColorAccent.copy(alpha = 0.85f))
                                .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                                .clickable { selectedCategory = category },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = category.displayName.uppercase(),
                                color = Color.White,
                                fontSize = 8.sp,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.ExtraBold,
                                lineHeight = 9.sp
                            )
                        }
                    }
                    
                    // Categoría Seleccionada
                    selectedCategory?.let { cat ->
                        AlertDialog(
                            onDismissRequest = { selectedCategory = null },
                            title = { Text(cat.displayName, color = ColorAccent, fontWeight = FontWeight.Bold) },
                            text = {
                                val filteredItems = state.inventory.items.filter { it.category == cat }
                                if (filteredItems.isEmpty()) {
                                    Text("No se encontraron objetos en esta categoría.")
                                } else {
                                    LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                                        items(filteredItems) { item: InventoryItem ->
                                            ListItem(
                                                headlineContent = { Text(item.name, fontSize = 14.sp) },
                                                trailingContent = { 
                                                    Text("x${item.quantity}", color = ColorAccent, fontWeight = FontWeight.Bold) 
                                                },
                                                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                                            )
                                            HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                                        }
                                    }
                                }
                            },
                            confirmButton = {
                                TextButton(onClick = { selectedCategory = null }) { Text("VOLVER", color = ColorAccent) }
                            },
                            shape = RoundedCornerShape(16.dp),
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    }
                }
                is InventoryUiState.Syncing -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = ColorAccent, strokeWidth = 2.dp)
                        Text("Buscando señal de PC...", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 12.dp))
                    }
                }
                is InventoryUiState.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red, modifier = Modifier.size(48.dp))
                        Text("SIN SEÑAL", fontWeight = FontWeight.Bold, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
                        Text(state.message, color = Color.Gray, textAlign = TextAlign.Center, fontSize = 12.sp)
                        Button(onClick = { viewModel.syncInventory() }, modifier = Modifier.padding(top = 16.dp)) {
                            Text("REINTENTAR")
                        }
                    }
                }
                else -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color.Gray.copy(alpha = 0.3f), modifier = Modifier.size(64.dp))
                        Text("SISTEMA LISTO", color = Color.Gray, letterSpacing = 2.sp, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}
