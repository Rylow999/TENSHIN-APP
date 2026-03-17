package com.tenshin.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.valentinilk.shimmer.shimmer
import com.tenshin.app.ui.theme.*

// ══════════════════════════════════════════
//  Modelo de datos del ítem de mercado
//  (mock hasta reemplazar con Retrofit)
// ══════════════════════════════════════════
data class MarketItem(
    val name:    String,
    val price:   Int,
    val change:  Float,    // porcentaje, ej. +12.4 o -3.2
    val trend:   String,   // "UP" | "DOWN" | "STABLE"
    val rank:    String?,
    val history: List<Float>,
)

val mockMarketItems = listOf(
    MarketItem("Arcane Grace",         285, +12.4f, "UP",     "R5", listOf(210f,220f,230f,225f,240f,260f,270f,275f,280f,285f)),
    MarketItem("Primary Deadhead",      95,  -3.2f, "DOWN",   "R5", listOf(110f,105f,100f,102f, 98f, 97f, 99f, 96f, 97f, 95f)),
    MarketItem("Exodia Contagion",      88,  +5.1f, "UP",     "R3", listOf( 70f, 72f, 75f, 78f, 80f, 82f, 83f, 85f, 87f, 88f)),
    MarketItem("Rhino Prime Blueprint", 29,  +1.0f, "STABLE", null, listOf( 27f, 28f, 28f, 29f, 28f, 29f, 30f, 29f, 29f, 29f)),
    MarketItem("Gara Prime Blueprint",  38,  +8.6f, "UP",     null, listOf( 28f, 30f, 32f, 33f, 35f, 36f, 37f, 38f, 38f, 38f)),
    MarketItem("Frost Prime Set",       42,  -1.5f, "DOWN",   null, listOf( 48f, 46f, 45f, 44f, 43f, 43f, 43f, 42f, 42f, 42f)),
)

fun MarketItem.trendColor(): Color = when (trend) {
    "UP"   -> ColorGreen
    "DOWN" -> ColorRed
    else   -> ColorTextMuted
}

fun MarketItem.priceEmoji(): String = when {
    price >= 200 -> "🔴"
    price >= 80  -> "🟠"
    price >= 30  -> "🟡"
    else         -> "🟢"
}

// ══════════════════════════════════════════
//  ItemCard
//  → Card + animateColorAsState para bg/border
//    Clic toggle selección → DetailPanel
// ══════════════════════════════════════════
@Composable
fun ItemCard(
    item:       MarketItem,
    selected:   Boolean,
    onSelect:   () -> Unit,
    modifier:   Modifier = Modifier,
    isLoading:  Boolean = false
) {
    val trendColor = item.trendColor()

    val bgColor by animateColorAsState(
        targetValue   = if (selected) ColorSurfaceElevated else ColorSurface,
        animationSpec = spring(),
        label         = "itemCard_bg",
    )
    val borderColor by animateColorAsState(
        targetValue   = if (selected) ColorAccent.copy(alpha = 0.4f) else ColorBorder,
        animationSpec = spring(),
        label         = "itemCard_border",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .then(if (isLoading) Modifier.shimmer() else Modifier)
            .drawBehind {
                drawRoundRect(
                    color        = borderColor,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx()),
                    style        = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx()),
                )
            }
            .background(bgColor, RoundedCornerShape(12.dp))
            .clickable { onSelect() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        if (selected) {
            Box(
                Modifier
                    .align(Alignment.CenterStart)
                    .width(3.dp)
                    .fillMaxHeight()
                    .background(ColorAccent, RoundedCornerShape(topStart = 3.dp, bottomStart = 3.dp))
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(item.priceEmoji(), fontSize = 13.sp)
                    Text(
                        text       = item.name,
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = ColorText,
                    )
                    item.rank?.let { rank ->
                        Text(
                            text     = rank,
                            fontSize = 10.sp,
                            color    = ColorRiven,
                            modifier = Modifier
                                .background(ColorRivenAlpha22, RoundedCornerShape(3.dp))
                                .padding(horizontal = 5.dp, vertical = 1.dp),
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text       = "${item.price}p",
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color      = ColorAccent,
                        fontFamily = FontFamily.Monospace,
                    )
                    val trendBg = when (item.trend) {
                        "UP"   -> ColorGreenAlpha22
                        "DOWN" -> ColorRedAlpha22
                        else   -> ColorTextMutedAlpha22
                    }
                    val trendArrow = when (item.trend) {
                        "UP"   -> "↑"
                        "DOWN" -> "↓"
                        else   -> "→"
                    }
                    val changeStr = if (item.change > 0) "+${item.change}%" else "${item.change}%"
                    Text(
                        text     = "$trendArrow $changeStr",
                        fontSize = 11.sp,
                        color    = trendColor,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .background(trendBg, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            MiniChart(
                data     = item.history,
                color    = trendColor,
                modifier = Modifier.size(width = 80.dp, height = 30.dp),
            )
        }
    }
}
