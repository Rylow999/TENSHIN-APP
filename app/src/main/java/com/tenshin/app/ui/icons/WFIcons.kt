package com.tenshin.app.ui.icons

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.random.Random

object WFIcons {

    private fun DrawScope.s(x: Float) = x * (size.width / 40f)
    private fun DrawScope.sv(y: Float) = y * (size.height / 40f)

    fun glitch(scope: DrawScope, color: Color) = with(scope) {
        val rand = Random(System.currentTimeMillis() / 150)
        repeat(6) {
            val x1 = rand.nextFloat() * 40f
            val y1 = rand.nextFloat() * 40f
            val length = rand.nextFloat() * 15f
            drawLine(
                color = color.copy(alpha = rand.nextFloat() * 0.7f),
                start = Offset(s(x1), sv(y1)),
                end = Offset(s(x1 + length), sv(y1)),
                strokeWidth = s(1.5f)
            )
        }
    }

    fun home(scope: DrawScope, color: Color) = with(scope) {
        val housePath = Path().apply {
            moveTo(s(20f), sv(4f)); lineTo(s(36f), sv(18f)); lineTo(s(36f), sv(36f))
            lineTo(s(4f),  sv(36f)); lineTo(s(4f),  sv(18f)); close()
        }
        drawPath(housePath, color.copy(alpha = 0.1f))
        drawPath(housePath, color, style = Stroke(width = s(1.5f), join = StrokeJoin.Round))
    }

    fun precio(scope: DrawScope, color: Color) = with(scope) {
        val hexPath = Path().apply {
            moveTo(s(20f), sv(2f)); lineTo(s(38f), sv(11f)); lineTo(s(38f), sv(29f))
            lineTo(s(20f), sv(38f)); lineTo(s(2f),  sv(29f)); lineTo(s(2f),  sv(11f)); close()
        }
        drawPath(hexPath, color.copy(alpha = 0.1f))
        drawPath(hexPath, color, style = Stroke(width = s(1.5f), join = StrokeJoin.Round))
    }

    fun inventario(scope: DrawScope, color: Color) = with(scope) {
        drawRect(color, topLeft = Offset(s(6f), sv(8f)), size = androidx.compose.ui.geometry.Size(s(28f), sv(24f)), style = Stroke(width = s(1.5f)))
    }

    fun plan(scope: DrawScope, color: Color) = with(scope) {
        val shieldPath = Path().apply {
            moveTo(s(20f), sv(3f)); lineTo(s(34f), sv(10f)); lineTo(s(34f), sv(22f)); close()
        }
        drawPath(shieldPath, color, style = Stroke(width = s(1.5f), join = StrokeJoin.Round))
    }

    fun rivens(scope: DrawScope, color: Color) = with(scope) {
        drawCircle(color, radius = s(14f), center = Offset(s(20f), sv(20f)), style = Stroke(width = s(1.5f)))
    }

    fun baro(scope: DrawScope, color: Color) = with(scope) {
        drawCircle(color, radius = s(14f), center = Offset(s(20f), sv(20f)), style = Stroke(width = s(1.5f)))
    }

    fun sesiones(scope: DrawScope, color: Color) = with(scope) {
        drawRect(color, topLeft = Offset(s(6f), sv(6f)), size = androidx.compose.ui.geometry.Size(s(28f), sv(28f)), style = Stroke(width = s(1.5f)))
    }

    fun ask(scope: DrawScope, color: Color) = glitch(scope, color)
    fun sync(scope: DrawScope, color: Color) = glitch(scope, color)
}
