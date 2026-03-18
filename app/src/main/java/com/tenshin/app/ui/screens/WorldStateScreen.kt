package com.tenshin.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tenshin.app.data.model.Inventory
import com.tenshin.app.data.remote.Fissure
import com.tenshin.app.data.remote.Invasion
import com.tenshin.app.data.remote.WorldStateResponse
import com.tenshin.app.ui.theme.*
import com.tenshin.app.ui.viewmodel.InventoryUiState
import com.tenshin.app.ui.viewmodel.InventoryViewModel
import com.tenshin.app.ui.viewmodel.WorldStateViewModel
import com.tenshin.app.ui.viewmodel.WorldUiState

@Composable
fun WorldStateScreen(
    viewModel: WorldStateViewModel = viewModel(),
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
            Text(
                text = "ESTADO DEL SISTEMA",
                style = MaterialTheme.typography.headlineSmall,
                color = ColorAccent,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { viewModel.refreshAll() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refrescar", tint = ColorAccent)
            }
        }

        Spacer(Modifier.height(16.dp))

        when (val state = uiState) {
            is WorldUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ColorAccent)
                }
            }
            is WorldUiState.Success -> {
                WorldStateContent(state, inventory)
            }
            is WorldUiState.Error -> {
                ErrorBox(state.message)
            }
        }
    }
}

@Composable
fun WorldStateContent(state: WorldUiState.Success, inventory: Inventory?) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { SectionHeader("CICLOS DEL SISTEMA") }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CycleChip(
                    "CETUS", 
                    if (state.worldState.cetusCycle?.isDay == true) "Día" else "Noche", 
                    if (state.worldState.cetusCycle?.isDay == true) ColorGold else ColorRiven, 
                    Modifier.weight(1f)
                )
                CycleChip(
                    "VALLIS", 
                    if (state.worldState.vallisCycle?.isWarm == true) "Cálido" else "Frío", 
                    if (state.worldState.vallisCycle?.isWarm == true) ColorGold else ColorAccent, 
                    Modifier.weight(1f)
                )
                CycleChip(
                    "CAMBION", 
                    state.worldState.cambionCycle?.active?.uppercase() ?: "???", 
                    if (state.worldState.cambionCycle?.active == "vome") ColorAccent else ColorRiven, 
                    Modifier.weight(1f)
                )
            }
        }

        item { SectionHeader("INVASIONES PRIORITARIAS") }
        val filteredInvasions = state.invasions.filter { !it.completed }.take(3)
        if (filteredInvasions.isEmpty()) {
            item { Text("No hay invasiones activas de interés.", color = ColorTextMuted, fontSize = 12.sp) }
        } else {
            items(filteredInvasions) { invasion ->
                InvasionCard(invasion, inventory)
            }
        }

        item { SectionHeader("FISURAS DEL VACÍO") }
        val prioritizedFissures = state.fissures.filter { it.active }.take(4)
        if (prioritizedFissures.isEmpty()) {
            item { Text("No hay fisuras activas detectadas.", color = ColorTextMuted, fontSize = 12.sp) }
        } else {
            items(prioritizedFissures) { fissure ->
                FissureCard(fissure, inventory)
            }
        }

        item {
            WorldGuiaCard(
                title = "DIRECTIVA DEL SISTEMA",
                content = getSmartRecommendation(state, inventory)
            )
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = ColorAccent.copy(alpha = 0.6f),
        letterSpacing = 2.sp,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
fun CycleChip(label: String, value: String, color: Color, modifier: Modifier) {
    Surface(
        color = ColorSurface,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = color)
            Text(value, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = ColorText)
        }
    }
}

@Composable
fun InvasionCard(invasion: Invasion, inventory: Inventory?) {
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
                Text(reward, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, color = if (isNeeded) ColorGold else ColorAccent)
                if (isNeeded) {
                    Text("¡REQUERIDO!", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = ColorGold)
                }
            }
        }
    }
}

@Composable
fun FissureCard(fissure: Fissure, inventory: Inventory?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ColorSurface)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(32.dp).background(ColorAccent.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(fissure.tier?.take(1) ?: "?", fontWeight = FontWeight.Bold, color = ColorAccent)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("${fissure.missionType ?: "Misión"} - ${fissure.node}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ColorText)
                Text(fissure.enemy ?: "Enemigo desconocido", fontSize = 10.sp, color = ColorTextMuted)
            }
            Text(fissure.tier?.uppercase() ?: "???", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = ColorAccent)
        }
    }
}

@Composable
fun WorldGuiaCard(title: String, content: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorAccent.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            .border(1.dp, ColorAccent.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, tint = ColorAccent, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ColorAccent)
            }
            Text(content, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp), color = ColorTextMuted, lineHeight = 18.sp)
        }
    }
}

fun getSmartRecommendation(state: WorldUiState.Success, inventory: Inventory?): String {
    if (inventory == null) return "Sincroniza tu arsenal para recibir directivas del Sistema Origen."
    
    val neededInvasion = state.invasions.find { inv ->
        val reward = inv.attackerReward?.itemString ?: inv.defenderReward?.itemString ?: ""
        inventory.items.none { it.name.contains(reward.split(" ").first(), ignoreCase = true) }
    }

    return when {
        neededInvasion != null -> "Prioridad: Invasión en ${neededInvasion.node}. Recompensa no detectada en tu inventario."
        state.worldState.cetusCycle?.isDay == false -> "Noche en Cetus. Buen momento para cazar Eidolons y mejorar tus Arcanos."
        else -> "Sistema estable. Recomendamos abrir fisuras Lith para completar sets de Prime básicos."
    }
}

@Composable
fun ErrorBox(message: String) {
    Box(
        Modifier.fillMaxWidth().background(Color.Red.copy(alpha = 0.1f), RoundedCornerShape(8.dp)).padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(message, color = Color.Red, textAlign = TextAlign.Center, fontSize = 12.sp)
    }
}
