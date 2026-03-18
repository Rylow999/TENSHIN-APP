package com.tenshin.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tenshin.app.ui.theme.*

data class MarketItem(
    val id: String,
    val name: String,
    val price: Int,
    val change: Int,
    val trend: String, // "UP", "DOWN", "STABLE"
    val history: List<Float>
) {
    fun trendColor() = when (trend) {
        "UP" -> ColorGreen
        "DOWN" -> ColorRed
        else -> ColorTextMuted
    }
}

@Composable
fun ItemCard(
    item: MarketItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val trendColor = item.trendColor()
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = ColorSurfaceElevated)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorText
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${if (item.change > 0) "+" else ""}${item.change}%",
                        fontSize = 10.sp,
                        color = trendColor,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(8.dp))
                    MiniChart(history = item.history, color = trendColor)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${item.price}p",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ColorAccent,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun MiniChart(history: List<Float>, color: Color) {
    Box(
        modifier = Modifier
            .width(40.dp)
            .height(16.dp)
            .background(color = ColorBg, shape = RoundedCornerShape(4.dp))
            .padding(2.dp)
    ) {
        // Simple visualization
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val min = history.min()
            val max = history.max()
            val range = (max - min).coerceAtLeast(1f)
            
            val path = androidx.compose.ui.graphics.Path()
            history.forEachIndexed { i, v ->
                val x = (i.toFloat() / (history.size - 1)) * w
                val y = h - ((v - min) / range) * h
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(
                path = path,
                color = color,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5.dp.toPx())
            )
        }
    }
}
