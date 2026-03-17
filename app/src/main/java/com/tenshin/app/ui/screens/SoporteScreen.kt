package com.tenshin.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tenshin.app.ui.theme.*

@Composable
fun SoporteScreen(isHacked: Boolean = false) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = if (isHacked) "ALIMENTAR AL_NEXO" else "ALIMENTAR AL ESPECTRO",
            style = MaterialTheme.typography.headlineSmall,
            color = if (isHacked) ColorHackerGreen else ColorGold,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = if (isHacked) "[DATA_INTACTA]: Tenshin es un proyecto de código abierto. Tu apoyo asegura que el Relé siga operando y que las transmisiones de 1999 no se pierdan." 
                   else "Tenshin es un proyecto de código abierto. Tu apoyo asegura que el Relé siga operando y que las transmisiones de 1999 no se pierdan.",
            fontSize = 13.sp,
            color = ColorTextMuted,
            lineHeight = 20.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // ── BARRA DE ENERGÍA DEL SERVIDOR ──
        ServerEnergyBar(current = 15.0, goal = 30.0, isHacked = isHacked)

        Spacer(Modifier.height(32.dp))

        // ── TIER CARDS ──
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                SupportTierCard(
                    title = "INICIADO DEL VACÍO",
                    price = "2.00 USD",
                    benefit = "Tu nombre en el Muro de Honor del Relé.",
                    color = if (isHacked) ColorHackerGreen else ColorAccent,
                    icon = Icons.Default.Star
                )
            }
            item {
                SupportTierCard(
                    title = "VIAJERO DE 1999",
                    price = "5.00 USD",
                    benefit = "Desbloquea permanentemente temas Retro y efectos de sonido exclusivos.",
                    color = if (isHacked) ColorHackerGreen else ColorHackerGreen,
                    icon = Icons.Default.Favorite
                )
            }
            item {
                SupportTierCard(
                    title = "MAESTRO ENTRATI",
                    price = "10.00 USD",
                    benefit = "Acceso anticipado a nuevas funciones de análisis de Rivens.",
                    color = if (isHacked) ColorHackerGreen else ColorGold,
                    icon = Icons.Default.Favorite
                )
            }
        }
        
        Spacer(Modifier.weight(1f))
        
        Text(
            text = "Las donaciones se procesan de forma externa para mantener la transparencia total.",
            fontSize = 10.sp,
            color = ColorTextDim,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        )
    }
}

@Composable
fun ServerEnergyBar(current: Double, goal: Double, isHacked: Boolean) {
    val progress = (current / goal).toFloat().coerceIn(0f, 1f)
    val accent = if (isHacked) ColorHackerGreen else ColorAccent
    
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(if (isHacked) "ESTADO_CARGA" else "ENERGÍA DEL RELÉ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = accent)
            Text("${current.toInt()} / ${goal.toInt()} USD", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ColorText)
        }
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .background(ColorSurface, RoundedCornerShape(6.dp))
                .border(1.dp, if (isHacked) ColorHackerGreen.copy(alpha = 0.3f) else ColorBorder, RoundedCornerShape(6.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .background(
                        Brush.horizontalGradient(listOf(accent.copy(alpha = 0.5f), accent)),
                        RoundedCornerShape(6.dp)
                    )
            )
        }
        Text(
            text = "Costo de mantenimiento mensual cubierto al ${(progress * 100).toInt()}%",
            fontSize = 10.sp,
            color = ColorTextMuted,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun SupportTierCard(title: String, price: String, benefit: String, color: Color, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ColorSurface),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color)
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = color)
                Text(benefit, fontSize = 12.sp, color = ColorTextMuted, lineHeight = 16.sp)
                Text(
                    text = "CONTRIBUIR: $price",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    color = ColorText,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
