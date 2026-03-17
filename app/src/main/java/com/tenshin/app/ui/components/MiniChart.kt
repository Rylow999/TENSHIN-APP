package com.tenshin.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke

// ══════════════════════════════════════════
//  MiniChart
//  → Canvas con drawLine (polyline) + drawCircle (punto final)
//  Equivale al componente SVG MiniChart del prototipo
// ══════════════════════════════════════════
@Composable
fun MiniChart(
    data:     List<Float>,
    color:    Color,
    modifier: Modifier = Modifier,
) {
    if (data.size < 2) return

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val maxV = data.max()
        val minV = data.min()
        val range = (maxV - minV).coerceAtLeast(1f)

        fun xOf(i: Int) = (i.toFloat() / (data.size - 1)) * w
        fun yOf(v: Float) = h - ((v - minV) / range) * h

        // Polyline
        val path = Path()
        data.forEachIndexed { i, v ->
            val x = xOf(i); val y = yOf(v)
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(
            path  = path,
            color = color,
            style = Stroke(width = 1.5f, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )

        // Punto final
        val lastX = xOf(data.size - 1)
        val lastY = yOf(data.last())
        drawCircle(color = color, radius = 2.5f, center = Offset(lastX, lastY))
    }
}
