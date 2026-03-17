package com.tenshin.app.ui.icons

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

// ══════════════════════════════════════════
//  WFIcons — íconos SVG convertidos a Canvas
//  Cada función recibe un DrawScope y un Color
//  Coordenadas normalizadas a viewBox 40×40
//  Para usar: Canvas(modifier) { scaleBy(size/40f); WFIcons.home(this, color) }
// ══════════════════════════════════════════
object WFIcons {

    // Escala las coordenadas de 40×40 al tamaño del Canvas
    private fun DrawScope.s(x: Float) = x * (size.width / 40f)
    private fun DrawScope.sv(y: Float) = y * (size.height / 40f)

    // ── home ─────────────────────────────────────────────────────────────────
    // polygon points="20,4 36,18 36,36 4,36 4,18"  (casa)
    // rect x=14 y=24 w=12 h=12                      (puerta)
    // circle cx=20 cy=18 r=3                         (ventana)
    // line x1=20 y1=4 x2=20 y2=8                    (chimenea)
    fun home(scope: DrawScope, color: Color) = with(scope) {
        val stroke = Stroke(width = s(1.5f), join = StrokeJoin.Round)
        val fillAlpha = color.copy(alpha = 0x18 / 255f)
        val housePath = Path().apply {
            moveTo(s(20f), sv(4f))
            lineTo(s(36f), sv(18f))
            lineTo(s(36f), sv(36f))
            lineTo(s(4f),  sv(36f))
            lineTo(s(4f),  sv(18f))
            close()
        }
        drawPath(housePath, fillAlpha)
        drawPath(housePath, color, style = stroke)

        // Puerta
        val doorFill = color.copy(alpha = 0x22 / 255f)
        drawRect(doorFill,  topLeft = Offset(s(14f), sv(24f)), size = androidx.compose.ui.geometry.Size(s(12f), sv(12f)))
        drawRect(color,     topLeft = Offset(s(14f), sv(24f)), size = androidx.compose.ui.geometry.Size(s(12f), sv(12f)),
            style = Stroke(width = s(1.2f)))

        // Ventana
        val circFill = color.copy(alpha = 0x33 / 255f)
        drawCircle(circFill, radius = s(3f), center = Offset(s(20f), sv(18f)))
        drawCircle(color,    radius = s(3f), center = Offset(s(20f), sv(18f)), style = Stroke(width = s(1.2f)))

        // Línea cima
        drawLine(color, start = Offset(s(20f), sv(4f)), end = Offset(s(20f), sv(8f)), strokeWidth = s(1.5f))
    }

    // ── precio ───────────────────────────────────────────────────────────────
    // polygon hexágono  points="20,2 38,11 38,29 20,38 2,29 2,11"
    // text "P"  (representado como drawText con canvas Paint — simplificado a líneas)
    // circle cx=20 cy=20 r=9  strokeDasharray
    fun precio(scope: DrawScope, color: Color) = with(scope) {
        val stroke15 = Stroke(width = s(1.5f), join = StrokeJoin.Round)
        val hexPath = Path().apply {
            moveTo(s(20f), sv(2f))
            lineTo(s(38f), sv(11f))
            lineTo(s(38f), sv(29f))
            lineTo(s(20f), sv(38f))
            lineTo(s(2f),  sv(29f))
            lineTo(s(2f),  sv(11f))
            close()
        }
        drawPath(hexPath, color.copy(alpha = 0x18 / 255f))
        drawPath(hexPath, color, style = stroke15)

        // Círculo punteado interior
        drawCircle(color, radius = s(9f), center = Offset(s(20f), sv(20f)),
            style = Stroke(width = s(0.8f)))

        // "P" representada con dos líneas (plumilla)
        drawLine(color, Offset(s(17f), sv(14f)), Offset(s(17f), sv(26f)), strokeWidth = s(1.5f), cap = StrokeCap.Round)
        drawLine(color, Offset(s(17f), sv(14f)), Offset(s(24f), sv(14f)), strokeWidth = s(1.5f), cap = StrokeCap.Round)
        drawLine(color, Offset(s(17f), sv(20f)), Offset(s(24f), sv(20f)), strokeWidth = s(1.5f), cap = StrokeCap.Round)
        drawArc(color, startAngle = -90f, sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(s(17f), sv(14f)),
            size = androidx.compose.ui.geometry.Size(s(14f), sv(12f)),
            style = Stroke(width = s(1.5f)))
    }

    // ── inventario ──────────────────────────────────────────────────────────
    // rect x=6 y=8 w=28 h=24 (tabla)  + filas + celdas
    fun inventario(scope: DrawScope, color: Color) = with(scope) {
        val s12 = Stroke(width = s(1.5f))
        drawRect(color.copy(alpha = 0x18 / 255f),
            topLeft = Offset(s(6f), sv(8f)), size = androidx.compose.ui.geometry.Size(s(28f), sv(24f)))
        drawRect(color, topLeft = Offset(s(6f), sv(8f)), size = androidx.compose.ui.geometry.Size(s(28f), sv(24f)),
            style = s12)

        // Líneas horizontales
        drawLine(color, Offset(s(6f), sv(14f)), Offset(s(34f), sv(14f)), strokeWidth = s(1f))
        drawLine(color.copy(alpha = 0.8f), Offset(s(6f), sv(20f)), Offset(s(34f), sv(20f)), strokeWidth = s(0.8f))
        drawLine(color.copy(alpha = 0.8f), Offset(s(6f), sv(26f)), Offset(s(34f), sv(26f)), strokeWidth = s(0.8f))

        // Celdas encabezado
        drawRect(color, topLeft = Offset(s(10f), sv(11f)), size = androidx.compose.ui.geometry.Size(s(4f), sv(4f)))
        drawLine(color, Offset(s(18f), sv(12f)), Offset(s(30f), sv(12f)), strokeWidth = s(1.2f))

        // Filas cuerpo
        drawLine(color.copy(alpha = 0.6f), Offset(s(10f), sv(22f)), Offset(s(26f), sv(22f)), strokeWidth = s(1f))
        drawLine(color.copy(alpha = 0.4f), Offset(s(10f), sv(28f)), Offset(s(22f), sv(28f)), strokeWidth = s(1f))
    }

    // ── plan ─────────────────────────────────────────────────────────────────
    // Escudo + checkmark
    fun plan(scope: DrawScope, color: Color) = with(scope) {
        val shieldPath = Path().apply {
            moveTo(s(20f), sv(3f))
            lineTo(s(34f), sv(10f))
            lineTo(s(34f), sv(22f))
            cubicTo(s(34f), sv(32f), s(20f), sv(37f), s(20f), sv(37f))
            cubicTo(s(20f), sv(37f), s(6f), sv(32f), s(6f), sv(22f))
            lineTo(s(6f), sv(10f))
            close()
        }
        drawPath(shieldPath, color.copy(alpha = 0x18 / 255f))
        drawPath(shieldPath, color, style = Stroke(width = s(1.5f), join = StrokeJoin.Round))

        // Checkmark
        val checkPath = Path().apply {
            moveTo(s(13f), sv(20f)); lineTo(s(17f), sv(24f)); lineTo(s(27f), sv(14f))
        }
        drawPath(checkPath, color, style = Stroke(width = s(2f), cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Punto central
        drawCircle(color.copy(alpha = 0.4f), radius = s(1.5f), center = Offset(s(20f), sv(20f)))
    }

    // ── rivens ──────────────────────────────────────────────────────────────
    // Estrella 10 puntos + círculo punteado + línea
    fun rivens(scope: DrawScope, color: Color) = with(scope) {
        val starPath = Path().apply {
            moveTo(s(20f), sv(4f))
            lineTo(s(24f), sv(14f)); lineTo(s(36f), sv(14f))
            lineTo(s(27f), sv(21f)); lineTo(s(30f), sv(32f))
            lineTo(s(20f), sv(26f)); lineTo(s(10f), sv(32f))
            lineTo(s(13f), sv(21f)); lineTo(s(4f),  sv(14f))
            lineTo(s(16f), sv(14f)); close()
        }
        drawPath(starPath, color.copy(alpha = 0x18 / 255f))
        drawPath(starPath, color, style = Stroke(width = s(1.5f), join = StrokeJoin.Round))

        // Círculo interior punteado
        drawCircle(color.copy(alpha = 0x22 / 255f), radius = s(4f), center = Offset(s(20f), sv(19f)))
        drawCircle(color, radius = s(4f), center = Offset(s(20f), sv(19f)), style = Stroke(width = s(1f)))

        // Línea superior
        drawLine(color.copy(alpha = 0.6f), Offset(s(20f), sv(8f)), Offset(s(20f), sv(14f)), strokeWidth = s(1.5f))
    }

    // ── baro ─────────────────────────────────────────────────────────────────
    // Círculos concéntricos + elipse + línea ecuador
    fun baro(scope: DrawScope, color: Color) = with(scope) {
        // Círculo exterior
        drawCircle(color.copy(alpha = 0x18 / 255f), radius = s(14f), center = Offset(s(20f), sv(20f)))
        drawCircle(color, radius = s(14f), center = Offset(s(20f), sv(20f)), style = Stroke(width = s(1.5f)))

        // Anillo medio
        drawCircle(color.copy(alpha = 0x22 / 255f), radius = s(8f), center = Offset(s(20f), sv(20f)))
        drawCircle(color, radius = s(8f), center = Offset(s(20f), sv(20f)), style = Stroke(width = s(1f)))

        // Punto central
        drawCircle(color, radius = s(3f), center = Offset(s(20f), sv(20f)))

        // Elipse meridiano
        val meridPath = Path().apply {
            moveTo(s(20f), sv(6f))
            cubicTo(s(26f), sv(10f), s(26f), sv(30f), s(20f), sv(34f))
            cubicTo(s(14f), sv(30f), s(14f), sv(10f), s(20f), sv(6f))
            close()
        }
        drawPath(meridPath, color, style = Stroke(width = s(1f)))

        // Ecuador
        drawLine(color.copy(alpha = 0.3f), Offset(s(6f), sv(20f)), Offset(s(34f), sv(20f)), strokeWidth = s(0.8f))
    }

    // ── sesiones ─────────────────────────────────────────────────────────────
    // Calendario con celdas
    fun sesiones(scope: DrawScope, color: Color) = with(scope) {
        // Cuerpo del calendario
        drawRect(color.copy(alpha = 0x18 / 255f),
            topLeft = Offset(s(6f), sv(6f)), size = androidx.compose.ui.geometry.Size(s(28f), sv(28f)))
        drawRect(color, topLeft = Offset(s(6f), sv(6f)), size = androidx.compose.ui.geometry.Size(s(28f), sv(28f)),
            style = Stroke(width = s(1.5f)))

        // Línea encabezado
        drawLine(color, Offset(s(6f), sv(13f)), Offset(s(34f), sv(13f)), strokeWidth = s(1f))

        // Grillas de citas (columnas)
        val cellSize = s(3f)
        val colX = listOf(s(11f), s(17f), s(23f), s(29f))
        val row1Y = sv(17f); val row2Y = sv(23f)
        val alphas1 = listOf(0.7f, 0.15f, 0.7f, 0.15f)
        val alphas2 = listOf(0.15f, 0.7f, 0.15f, 0.7f)
        for (i in colX.indices) {
            drawRect(color.copy(alpha = alphas1[i]), topLeft = Offset(colX[i], row1Y), size = androidx.compose.ui.geometry.Size(cellSize, cellSize))
            drawRect(color.copy(alpha = alphas2[i]), topLeft = Offset(colX[i], row2Y), size = androidx.compose.ui.geometry.Size(cellSize, cellSize))
        }

        // Tabs del calendario
        drawRect(color, topLeft = Offset(s(11f), sv(4f)), size = androidx.compose.ui.geometry.Size(s(3f), sv(6f)))
        drawRect(color, topLeft = Offset(s(26f), sv(4f)), size = androidx.compose.ui.geometry.Size(s(3f), sv(6f)))
    }

    // ── ask ──────────────────────────────────────────────────────────────────
    // Burbuja de diálogo + tres puntos
    fun ask(scope: DrawScope, color: Color) = with(scope) {
        val bubblePath = Path().apply {
            // Burbuja redondeada con cola
            moveTo(s(10f), sv(4f))
            lineTo(s(30f), sv(4f))
            cubicTo(s(34f), sv(4f), s(34f), sv(8f), s(34f), sv(8f))
            lineTo(s(34f), sv(24f))
            cubicTo(s(34f), sv(28f), s(30f), sv(28f), s(30f), sv(28f))
            lineTo(s(22f), sv(28f))
            lineTo(s(14f), sv(36f))
            lineTo(s(14f), sv(28f))
            lineTo(s(10f), sv(28f))
            cubicTo(s(6f), sv(28f), s(6f), sv(24f), s(6f), sv(24f))
            lineTo(s(6f), sv(8f))
            cubicTo(s(6f), sv(4f), s(10f), sv(4f), s(10f), sv(4f))
            close()
        }
        drawPath(bubblePath, color.copy(alpha = 0x18 / 255f))
        drawPath(bubblePath, color, style = Stroke(width = s(1.5f), join = StrokeJoin.Round))

        // Tres puntos
        listOf(s(15f), s(20f), s(25f)).forEach { cx ->
            drawCircle(color, radius = s(1.5f), center = Offset(cx, sv(16f)))
        }
    }
}
