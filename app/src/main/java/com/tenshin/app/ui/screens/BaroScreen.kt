package com.tenshin.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tenshin.app.data.remote.VoidTraderItem
import com.tenshin.app.data.remote.VoidTraderResponse
import com.tenshin.app.ui.theme.*
import com.tenshin.app.ui.viewmodel.BaroUiState
import com.tenshin.app.ui.viewmodel.BaroViewModel

@Composable
fun BaroScreen(viewModel: BaroViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        when (val state = uiState) {
            is BaroUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ColorGold)
                }
            }
            is BaroUiState.Success -> {
                BaroContent(state.baroData, onRefresh = { viewModel.fetchBaroData() })
            }
            is BaroUiState.Error -> {
                ErrorView(state.message, onRetry = { viewModel.fetchBaroData() })
            }
        }
    }
}

@Composable
fun BaroContent(data: VoidTraderResponse, onRefresh: () -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (data.active) "INVENTARIO ACTUAL" else "INVENTARIO ESTIMADO",
                fontSize = 10.sp,
                color = ColorGold,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            IconButton(onClick = onRefresh) {
                Icon(Icons.Default.Refresh, contentDescription = "Refrescar", tint = ColorGold)
            }
        }

        BaroHeader(
            arrival = if (data.active) data.expiry else data.activation,
            location = data.location,
            isActive = data.active
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            if (data.inventory.isEmpty()) {
                item {
                    Text(
                        text = "Baro aún no ha llegado. El inventario se mostrará aquí cuando esté presente.",
                        color = ColorTextMuted,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp)
                    )
                }
            } else {
                items(data.inventory) { item ->
                    BaroItemCard(item)
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                BaroTipCard()
            }
        }
    }
}

@Composable
fun BaroHeader(arrival: String, location: String, isActive: Boolean) {
    // Limpieza de fecha para mostrar algo más amigable (simplificado)
    val timeLabel = if (isActive) "SE VA EN" else "LLEGA EN"
    val displayTime = arrival.substringBefore("T").replace("-", "/") // Formato rápido

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(ColorGold.copy(alpha = 0.15f), Color.Transparent)
                )
            )
            .border(1.dp, ColorGold.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
            .padding(24.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Icon(
                Icons.Default.ShoppingCart,
                contentDescription = null,
                tint = ColorGold,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "BARO KI'TEER",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ColorGold,
                letterSpacing = 4.sp
            )
            Text(
                text = "EL COMERCIANTE DEL VACÍO",
                fontSize = 10.sp,
                color = ColorGold.copy(alpha = 0.6f),
                letterSpacing = 2.sp
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InfoColumn(label = "ESTADO", value = if (isActive) "PRESENTE" else "EN RUTA")
                InfoColumn(label = timeLabel, value = displayTime)
                InfoColumn(label = "UBICACIÓN", value = location.ifEmpty { "Desconocida" })
            }
        }
    }
}

@Composable
fun BaroItemCard(item: VoidTraderItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ColorSurface),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, ColorGold.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.item, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ColorText)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${item.ducats} duca", fontSize = 10.sp, color = ColorGold, fontFamily = FontFamily.Monospace)
                    Text("${item.credits} cred", fontSize = 10.sp, color = ColorTextMuted, fontFamily = FontFamily.Monospace)
                }
            }
            
            val tag = when {
                item.item.contains("Primed") -> "INDISPENSABLE"
                item.item.contains("Prisma") -> "ESPECIAL"
                else -> "COLECCIÓN"
            }

            Surface(
                color = if(tag == "INDISPENSABLE") ColorGreen.copy(alpha = 0.1f) else ColorGold.copy(alpha = 0.1f),
                shape = RoundedCornerShape(4.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if(tag == "INDISPENSABLE") ColorGreen.copy(alpha = 0.4f) else ColorGold.copy(alpha = 0.4f))
            ) {
                Text(
                    text = tag,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = if(tag == "INDISPENSABLE") ColorGreen else ColorGold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun ErrorView(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(message, color = Color.Red, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = ColorGold)) {
            Text("Reintentar")
        }
    }
}

@Composable
fun InfoColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 8.sp, color = ColorTextMuted, fontWeight = FontWeight.Bold)
        Text(value, fontSize = 11.sp, color = ColorText, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun BaroTipCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorGold.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            .border(1.dp, ColorGold.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Icon(Icons.Default.Info, contentDescription = null, tint = ColorGold, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Consejo: Prioriza siempre los Mods Primed de continuidad y flujo si no los tienes. Son la base de casi todas las builds de Warframe.",
                fontSize = 11.sp,
                color = ColorTextMuted,
                lineHeight = 16.sp
            )
        }
    }
}
