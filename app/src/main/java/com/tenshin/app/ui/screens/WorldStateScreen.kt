package com.tenshin.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tenshin.app.ui.theme.ColorAccent
import com.tenshin.app.ui.theme.ColorGold

@Composable
fun WorldStateScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "ESTADO DEL MUNDO",
            style = MaterialTheme.typography.headlineSmall,
            color = ColorAccent,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item { WorldCard("CETUS / LLANURAS DE EIDOLON", "Día (15m restante)", "Pesca: Mortus Lungfish (Noche), Trall (Día).\nMinería: Auroxium, Nyth.") }
            item { WorldCard("FORTUNA / VALLES DE ORB", "Frío (5m restante)", "Pesca: Longwinder (Cálido), Tromyzon (Frío).\nMinería: Phasmin, Zodian.") }
            item { WorldCard("NECRALISK / CAMBAS DE CAMBION", "Vome (Activo)", "Pesca: Glutinox, Duat.\nMinería: Namalon, Thaumica.") }
            
            item {
                InfoSection(
                    title = "GUÍA DE PESCA",
                    content = "Los peces raros suelen aparecer en 'puntos calientes' (burbujas en el agua). Usa cebos específicos y el arpón adecuado según el planeta."
                )
            }
            item {
                InfoSection(
                    title = "CONSEJO DE MINERÍA",
                    content = "Las vetas azules contienen gemas, las amarillas contienen metales. El Cortador Sunpoint es el más eficiente."
                )
            }
        }
    }
}

@Composable
fun WorldCard(title: String, state: String, details: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = ColorAccent)
            Text(state, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ColorGold, modifier = Modifier.padding(vertical = 4.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 8.dp))
            Text(details, fontSize = 12.sp, lineHeight = 18.sp)
        }
    }
}

@Composable
fun InfoSection(title: String, content: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorAccent.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
            .border(1.dp, ColorAccent.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, tint = ColorAccent, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = ColorAccent)
            }
            Text(content, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp), color = Color.Gray)
        }
    }
}
