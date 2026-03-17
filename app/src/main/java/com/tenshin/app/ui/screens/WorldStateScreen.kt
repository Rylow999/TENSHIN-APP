package com.tenshin.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tenshin.app.ui.theme.*

@Composable
fun WorldStateScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "SISTEMA ORIGEN",
            style = MaterialTheme.typography.headlineSmall,
            color = ColorAccent,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item { 
                InteractiveWorldCard(
                    planet = "CETUS", 
                    region = "LLANURAS DE EIDOLON", 
                    state = "Día (12m restantes)", 
                    details = "Pesca: Mortus Lungfish (Noche), Trall (Día).\nMinería: Auroxium, Nyth.",
                    accent = ColorGold
                ) 
            }
            item { 
                InteractiveWorldCard(
                    planet = "FORTUNA", 
                    region = "VALLES DE ORB", 
                    state = "Frío (Activo)", 
                    details = "Pesca: Longwinder (Cálido), Tromyzon (Frío).\nMinería: Phasmin, Zodian.",
                    accent = ColorAccent
                ) 
            }
            item { 
                InteractiveWorldCard(
                    planet = "NECRALISK", 
                    region = "CAMBAS DE CAMBION", 
                    state = "Vome (Activo)", 
                    details = "Pesca: Glutinox, Duat.\nMinería: Namalon, Thaumica.",
                    accent = ColorRiven
                ) 
            }
            
            item {
                GuiaCard(
                    title = "GUÍA DE SUPERVIVENCIA",
                    content = "Usa el Arpón Lanzo para peces de las llanuras. En los Valles, el Arpón Shockprod es vital. Las vetas de minería con 'brillo' extra dan gemas raras."
                )
            }
        }
    }
}

@Composable
fun InteractiveWorldCard(planet: String, region: String, state: String, details: String, accent: Color) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ColorSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, accent.copy(alpha = 0.3f)),
        onClick = { expanded = !expanded }
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(planet, fontWeight = FontWeight.ExtraBold, fontSize = 10.sp, color = accent, letterSpacing = 2.sp)
                    Text(region, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ColorText)
                }
                Text(state, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = accent)
            }
            
            AnimatedVisibility(visible = expanded) {
                Column(Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                    Spacer(Modifier.height(12.dp))
                    Text(details, fontSize = 12.sp, lineHeight = 18.sp, color = ColorTextMuted)
                    Spacer(Modifier.height(8.dp))
                    Text("TOCA PARA MINIMIZAR", fontSize = 9.sp, color = accent.copy(alpha = 0.5f), modifier = Modifier.align(Alignment.End))
                }
            }
        }
    }
}

@Composable
fun GuiaCard(title: String, content: String) {
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
