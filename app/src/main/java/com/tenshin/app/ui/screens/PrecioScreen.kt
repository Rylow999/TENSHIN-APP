package com.tenshin.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tenshin.app.ui.components.DetailPanel
import com.tenshin.app.ui.components.ItemCard
import com.tenshin.app.ui.components.mockMarketItems
import com.tenshin.app.ui.theme.*

// ══════════════════════════════════════════
//  PrecioScreen
//  → SearchBar (BasicTextField)
//    FilterChips (Todos / Arcanos / Planos Prime / Recursos)
//    LazyColumn de ItemCards
//    DetailPanel con AnimatedVisibility
// ══════════════════════════════════════════
@Composable
fun PrecioScreen() {
    var query       by remember { mutableStateOf("") }
    var activeFilter by remember { mutableStateOf("Todos") }
    var selectedName by remember { mutableStateOf<String?>(null) }

    val filtered = mockMarketItems.filter {
        it.name.contains(query, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // ── SearchBar ──
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .background(ColorSurface, RoundedCornerShape(12.dp))
                .drawBehind {
                    drawRoundRect(
                        color        = ColorBorder,
                        cornerRadius = CornerRadius(12.dp.toPx()),
                        style        = Stroke(width = 1.dp.toPx()),
                    )
                }
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Text("🔍", fontSize = 16.sp, color = ColorTextMuted)
            BasicTextField(
                value         = query,
                onValueChange = { query = it },
                singleLine    = true,
                textStyle     = TextStyle(color = ColorText, fontSize = 13.sp, fontFamily = FontFamily.Default),
                cursorBrush   = SolidColor(ColorAccent),
                modifier      = Modifier.weight(1f),
                decorationBox = { inner ->
                    if (query.isEmpty()) {
                        Text("Buscar ítem en el Relé...", fontSize = 13.sp, color = ColorTextMuted)
                    }
                    inner()
                },
            )
        }

        // ── Filter chips ──
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(bottom = 14.dp),
        ) {
            listOf("Todos", "Arcanos", "Planos Prime", "Recursos").forEach { filter ->
                val isActive = filter == activeFilter
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .background(
                            if (isActive) ColorAccent.copy(alpha = 0.13f) else androidx.compose.ui.graphics.Color.Transparent,
                            RoundedCornerShape(20.dp),
                        )
                        .drawBehind {
                            drawRoundRect(
                                color        = if (isActive) ColorAccent else ColorBorder,
                                cornerRadius = CornerRadius(20.dp.toPx()),
                                style        = Stroke(width = 1.dp.toPx()),
                            )
                        }
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                        ) { activeFilter = filter },
                ) {
                    Text(filter, fontSize = 11.sp, color = if (isActive) ColorAccent else ColorTextMuted)
                }
            }
        }

        // ── Lista de ítems + DetailPanel ──
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filtered, key = { it.name }) { item ->
                ItemCard(
                    item     = item,
                    selected = selectedName == item.name,
                    onSelect = {
                        selectedName = if (selectedName == item.name) null else item.name
                    },
                )
            }
            item {
                val sel = filtered.find { it.name == selectedName }
                DetailPanel(
                    item    = sel,
                    onClose = { selectedName = null },
                )
            }
        }
    }
}
