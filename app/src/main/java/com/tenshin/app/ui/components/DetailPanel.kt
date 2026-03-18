package com.tenshin.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tenshin.app.ui.theme.*

@Composable
fun DetailPanel(
    item:      MarketItem?,
    onClose:   () -> Unit,
    modifier:  Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = item != null,
        enter   = expandVertically() + fadeIn(),
        exit    = shrinkVertically() + fadeOut(),
        modifier = modifier,
    ) {
        if (item == null) return@AnimatedVisibility

        val trendColor = item.trendColor()
        val maxV = item.history.max()
        val minV = item.history.min()
        val avg  = item.history.average().toInt()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .background(color = ColorSurfaceElevated, shape = RoundedCornerShape(16.dp))
                .padding(16.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.Top,
                modifier              = Modifier.fillMaxWidth(),
            ) {
                Column {
                    Text(item.name,  fontSize = 15.sp, fontWeight = FontWeight.Bold, color = ColorText)
                    Text("${item.price}p",
                        fontSize   = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = ColorAccent,
                        fontFamily = FontFamily.Monospace,
                    )
                }
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = ColorTextMuted)
                }
            }

            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(color = ColorBg, shape = RoundedCornerShape(10.dp))
                    .padding(horizontal = 8.dp, vertical = 10.dp),
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width; val h = size.height
                    val range = (maxV - minV).coerceAtLeast(1f)
                    val count = item.history.size

                    fun xOf(i: Int) = (i.toFloat() / (count - 1)) * w
                    fun yOf(v: Float) = h - ((v - minV) / range) * (h - 10f) - 5f

                    val linePath = Path()
                    item.history.forEachIndexed { i, v ->
                        if (i == 0) linePath.moveTo(xOf(i), yOf(v))
                        else linePath.lineTo(xOf(i), yOf(v))
                    }

                    val areaPath = Path()
                    areaPath.addPath(linePath)
                    areaPath.lineTo(w, h); areaPath.lineTo(0f, h); areaPath.close()

                    drawPath(
                        path = areaPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(trendColor.copy(alpha = 0.25f), trendColor.copy(alpha = 0f)),
                        ),
                    )

                    drawPath(
                        path  = linePath,
                        color = trendColor,
                        style = Stroke(width = 2f, cap = StrokeCap.Round, join = StrokeJoin.Round),
                    )

                    val lastIdx = count - 1
                    drawCircle(trendColor, radius = 4f, center = Offset(xOf(lastIdx), yOf(item.history.last())))
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 12.dp),
            ) {
                listOf("90d","75d","60d","45d","30d","15d","hoy").forEach { label ->
                    Text(label, fontSize = 9.sp, color = ColorTextDim, fontFamily = FontFamily.Monospace)
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            ) {
                listOf(
                    Triple("MÁXIMO",   "${maxV.toInt()}p", ColorGreen),
                    Triple("PROMEDIO", "${avg}p",           ColorAccent),
                    Triple("MÍNIMO",   "${minV.toInt()}p", ColorRed),
                ).forEach { (label, value, color) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .background(color = ColorBg, shape = RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                    ) {
                        Text(label,  fontSize = 9.sp,  color = ColorTextDim,  letterSpacing = 1.sp, textAlign = TextAlign.Center)
                        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color,
                            fontFamily = FontFamily.Monospace, textAlign = TextAlign.Center)
                    }
                }
            }

            val spectrumText = when (item.trend) {
                "UP"   -> "Las corrientes del Relé favorecen a ${item.name}. Subió ${item.change}% — momento de soltar, Tenno."
                "DOWN" -> "Las mareas bajan para ${item.name}. Caída de ${Math.abs(item.change)}% — esperá la marea, Tenno."
                else   -> "El mercado permanece estable. Lista y esperá movimiento, Tenno."
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = ColorBg, shape = RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                Text("🔮", fontSize = 16.sp)
                Column {
                    Text("ANÁLISIS DEL ESPECTRO",
                        fontSize      = 10.sp,
                        color         = ColorTextMuted,
                        letterSpacing = 0.5.sp,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(spectrumText, fontSize = 12.sp, color = ColorText, lineHeight = 18.sp)
                }
            }
        }
    }
}
