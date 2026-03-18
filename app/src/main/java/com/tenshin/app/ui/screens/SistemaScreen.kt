package com.tenshin.app.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tenshin.app.data.model.Inventory
import com.tenshin.app.data.remote.Fissure
import com.tenshin.app.data.remote.Invasion
import com.tenshin.app.ui.theme.*
import com.tenshin.app.ui.viewmodel.InventoryUiState
import com.tenshin.app.ui.viewmodel.InventoryViewModel
import com.tenshin.app.ui.viewmodel.SistemaViewModel
import com.tenshin.app.ui.viewmodel.SistemaUiState

@Composable
fun SistemaScreen(
    viewModel: SistemaViewModel = viewModel(),
    inventoryViewModel: InventoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val invState by inventoryViewModel.uiState.collectAsState()
    val inventory = (invState as? InventoryUiState.Success)?.inventory

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "ESTADO DEL SISTEMA",
                    style = MaterialTheme.typography.headlineSmall,
                    color = if ((uiState as? SistemaUiState.Success)?.isSteelPath == true) ColorRed else ColorAccent,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if ((uiState as? SistemaUiState.Success)?.isSteelPath == true) "MODO CAMINO DE ACERO ACTIVO" else "SISTEMA ORIGEN ESTÁNDAR",
                    fontSize = 10.sp,
                    color = ColorTextMuted
                )
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.toggleSteelPath() }) {
                    Icon(
                        Icons.Default.Security, 
                        contentDescription = "Steel Path", 
                        tint = if ((uiState as? SistemaUiState.Success)?.isSteelPath == true) ColorRed else ColorTextMuted
                    )
                }
                IconButton(onClick = { viewModel.refreshAll() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refrescar", tint = ColorAccent)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        when (val state = uiState) {
            is SistemaUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ColorAccent)
                }
            }
            is SistemaUiState.Success -> {
                SistemaContent(state, inventory)
            }
            is SistemaUiState.Error -> {
                SistemaErrorBox(state.message)
            }
        }
    }
}

@Composable
fun SistemaContent(state: SistemaUiState.Success, inventory: Inventory?) {
    val accentColor = if (state.isSteelPath) ColorRed else ColorAccent
    
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { SistemaSectionHeader("CICLOS DEL SISTEMA") }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SistemaCycleChip("CETUS", if (state.worldState.cetusCycle?.isDay == true) "Día" else "Noche", if (state.worldState.cetusCycle?.isDay == true) ColorGold else ColorRiven, Modifier.weight(1f), accentColor)
                SistemaCycleChip("VALLIS", if (state.worldState.vallisCycle?.isWarm == true) "Cálido" else "Frío", if (state.worldState.vallisCycle?.isWarm == true) ColorGold else accentColor, Modifier.weight(1f), accentColor)
                SistemaCycleChip("CAMBION", state.worldState.cambionCycle?.active?.uppercase() ?: "???", if (state.worldState.cambionCycle?.active == "vome") ColorAccent else ColorRiven, Modifier.weight(1f), accentColor)
            }
        }

        if (state.isSteelPath) {
            item { SistemaSectionHeader("HONORES DE CAMINO DE ACERO") }
            item { SteelPathCard() }
        }

        item { SistemaSectionHeader("INVASIONES CRÍTICAS") }
        val filteredInvasions = state.invasions.filter { !it.completed }.take(3)
        items(filteredInvasions) { invasion ->
            SistemaInvasionCard(invasion, inventory, accentColor)
        }

        item { SistemaSectionHeader("FISURAS ACTIVAS") }
        val prioritizedFissures = state.fissures.filter { it.active }.take(4)
        items(prioritizedFissures) { fissure ->
            SistemaFissureCard(fissure, inventory, accentColor)
        }

        item {
            SistemaGuiaCard(
                title = "DIRECTIVA DEL SISTEMA",
                content = getSistemaRecommendation(state, inventory),
                accentColor = accentColor
            )
        }
    }
}

@Composable
fun SistemaSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        color = ColorTextMuted,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
fun SistemaCycleChip(label: String, value: String, color: Color, modifier: Modifier, accent: Color) {
    Surface(
        color = ColorSurface,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier.drawBehind {
            drawRoundRect(
                color = color.copy(alpha = 0.1f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx())
            )
        }
    ) {
        Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = color)
            Text(value, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = ColorText)
        }
    }
}

@Composable
fun SteelPathCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ColorSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, ColorRed.copy(alpha = 0.5f))
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(40.dp).background(ColorRed.copy(0.1f), CircleShape), contentAlignment = Alignment.Center) {
                Text("SP", color = ColorRed, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text("RECOMPENSA SEMANAL", fontSize = 10.sp, color = ColorRed, fontWeight = FontWeight.Bold)
                Text("Adaptador de Arcano Primaria", fontSize = 14.sp, color = ColorText, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SistemaInvasionCard(invasion: Invasion, inventory: Inventory?, accent: Color) {
    val reward = invasion.attackerReward?.itemString ?: invasion.defenderReward?.itemString ?: "Desconocido"
    val isNeeded = inventory?.items?.none { it.name.contains(reward.split(" ").first(), ignoreCase = true) } ?: false

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ColorSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isNeeded) ColorGold.copy(alpha = 0.4f) else ColorBorder)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(invasion.node, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ColorText)
                Text(invasion.desc ?: "Sin descripción", fontSize = 10.sp, color = ColorTextMuted)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(reward, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, color = if (isNeeded) ColorGold else accent)
                if (isNeeded) {
                    Text("¡REQUERIDO!", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = ColorGold)
                }
            }
        }
    }
}

@Composable
fun SistemaFissureCard(fissure: Fissure, inventory: Inventory?, accent: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ColorSurface)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(32.dp).background(accent.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(fissure.tier?.take(1) ?: "?", fontWeight = FontWeight.Bold, color = accent)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("${fissure.missionType ?: "Misión"} - ${fissure.node}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ColorText)
                Text(fissure.enemy ?: "Enemigo desconocido", fontSize = 10.sp, color = ColorTextMuted)
            }
            Text(fissure.tier?.uppercase() ?: "???", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = accent)
        }
    }
}

@Composable
fun SistemaGuiaCard(title: String, content: String, accentColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(accentColor.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            .border(1.dp, accentColor.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = accentColor)
            }
            Text(content, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp), color = ColorTextMuted, lineHeight = 18.sp)
        }
    }
}

fun getSistemaRecommendation(state: SistemaUiState.Success, inventory: Inventory?): String {
    if (inventory == null) return "Sincroniza tu arsenal para recibir directivas del Sistema Origen."
    
    val neededInvasion = state.invasions.find { inv ->
        val reward = inv.attackerReward?.itemString ?: inv.defenderReward?.itemString ?: ""
        inventory.items.none { it.name.contains(reward.split(" ").first(), ignoreCase = true) }
    }

    return when {
        state.isSteelPath -> "Riesgo detectado: Camino de Acero activo. Prioriza completar los Honores Semanales para Esencia de Acero."
        neededInvasion != null -> "Atención: Invasión crítica en ${neededInvasion.node}. Tu inventario carece de esta recompensa."
        state.worldState.cetusCycle?.isDay == false -> "Noche detectada. Los Eidolons están activos en las Llanuras."
        else -> "Sistema estable. Recomendamos optimizar sets Prime en fisuras Lith/Meso."
    }
}

@Composable
fun SistemaErrorBox(message: String) {
    Box(
        Modifier.fillMaxWidth().background(Color.Red.copy(alpha = 0.1f), RoundedCornerShape(8.dp)).padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(message, color = Color.Red, textAlign = TextAlign.Center, fontSize = 12.sp)
    }
}
