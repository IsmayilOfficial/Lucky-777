package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.SlotSymbol

/**
 * Custom vector-crisp Canvas artwork for each slot symbol so visuals stay sharp
 * and consistent across all Android devices and screen sizes.
 */
@Composable
fun SlotSymbolArtwork(
    symbol: SlotSymbol,
    modifier: Modifier = Modifier,
    iconSize: Dp = 56.dp,
    includeContentDescription: Boolean = true,
) {
    val semanticsModifier = if (includeContentDescription) {
        modifier.semantics { contentDescription = symbol.displayName }
    } else {
        modifier
    }

    Canvas(modifier = semanticsModifier.size(iconSize)) {
        val w = size.width
        val h = size.height

        when (symbol) {
            SlotSymbol.CHERRY -> drawCherries(w, h)
            SlotSymbol.LEMON -> drawLemon(w, h)
            SlotSymbol.BELL -> drawBell(w, h)
            SlotSymbol.SEVEN -> drawLuckySeven(w, h)
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCherries(w: Float, h: Float) {
    // Curved stems meeting near top
    val stemPath = Path().apply {
        moveTo(w * 0.30f, h * 0.58f)
        quadraticTo(w * 0.42f, h * 0.25f, w * 0.56f, h * 0.16f)
        moveTo(w * 0.70f, h * 0.62f)
        quadraticTo(w * 0.62f, h * 0.32f, w * 0.56f, h * 0.16f)
    }
    drawPath(
        path = stemPath,
        color = Color(0xFF4ADE80),
        style = Stroke(width = w * 0.065f, cap = StrokeCap.Round, join = StrokeJoin.Round),
    )

    // Leaf
    val leafPath = Path().apply {
        moveTo(w * 0.56f, h * 0.16f)
        quadraticTo(w * 0.78f, h * 0.10f, w * 0.80f, h * 0.26f)
        quadraticTo(w * 0.65f, h * 0.30f, w * 0.56f, h * 0.16f)
    }
    drawPath(leafPath, color = Color(0xFF22C55E))

    // Left cherry
    val leftCenter = Offset(w * 0.31f, h * 0.67f)
    val radius = w * 0.19f
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFF5C8A), Color(0xFFE11D48), Color(0xFF881337)),
            center = Offset(leftCenter.x - radius * 0.25f, leftCenter.y - radius * 0.25f),
            radius = radius * 1.3f,
        ),
        radius = radius,
        center = leftCenter,
    )
    drawCircle(
        color = Color.White.copy(alpha = 0.65f),
        radius = radius * 0.22f,
        center = Offset(leftCenter.x - radius * 0.32f, leftCenter.y - radius * 0.32f),
    )

    // Right cherry
    val rightCenter = Offset(w * 0.68f, h * 0.71f)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFF5C8A), Color(0xFFE11D48), Color(0xFF881337)),
            center = Offset(rightCenter.x - radius * 0.25f, rightCenter.y - radius * 0.25f),
            radius = radius * 1.3f,
        ),
        radius = radius,
        center = rightCenter,
    )
    drawCircle(
        color = Color.White.copy(alpha = 0.65f),
        radius = radius * 0.22f,
        center = Offset(rightCenter.x - radius * 0.32f, rightCenter.y - radius * 0.32f),
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawLemon(w: Float, h: Float) {
    val lemonPath = Path().apply {
        moveTo(w * 0.16f, h * 0.56f)
        cubicTo(w * 0.18f, h * 0.25f, w * 0.58f, h * 0.18f, w * 0.84f, h * 0.42f)
        cubicTo(w * 0.88f, h * 0.47f, w * 0.88f, h * 0.53f, w * 0.84f, h * 0.58f)
        cubicTo(w * 0.76f, h * 0.84f, w * 0.32f, h * 0.86f, w * 0.16f, h * 0.60f)
        close()
    }
    drawPath(
        path = lemonPath,
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFFFEF08A), Color(0xFFFACC15), Color(0xFFCA8A04)),
            start = Offset(w * 0.2f, h * 0.2f),
            end = Offset(w * 0.8f, h * 0.85f),
        ),
    )
    drawPath(
        path = lemonPath,
        color = Color(0xFFFDE047),
        style = Stroke(width = w * 0.03f),
    )

    // Small green leaf at top-right
    val leafPath = Path().apply {
        moveTo(w * 0.68f, h * 0.28f)
        quadraticTo(w * 0.86f, h * 0.14f, w * 0.88f, h * 0.32f)
        quadraticTo(w * 0.74f, h * 0.36f, w * 0.68f, h * 0.28f)
    }
    drawPath(leafPath, color = Color(0xFF22C55E))

    // Specular shine
    drawOval(
        color = Color.White.copy(alpha = 0.55f),
        topLeft = Offset(w * 0.32f, h * 0.36f),
        size = Size(w * 0.22f, h * 0.09f),
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBell(w: Float, h: Float) {
    // Clapper at bottom
    drawCircle(
        color = Color(0xFFD97706),
        radius = w * 0.095f,
        center = Offset(w * 0.5f, h * 0.80f),
    )

    // Top crown ring
    drawCircle(
        color = Color(0xFFFDE047),
        radius = w * 0.07f,
        center = Offset(w * 0.5f, h * 0.16f),
    )

    // Bell dome
    val bellPath = Path().apply {
        moveTo(w * 0.5f, h * 0.20f)
        cubicTo(w * 0.25f, h * 0.22f, w * 0.24f, h * 0.56f, w * 0.15f, h * 0.71f)
        lineTo(w * 0.85f, h * 0.71f)
        cubicTo(w * 0.76f, h * 0.56f, w * 0.75f, h * 0.22f, w * 0.5f, h * 0.20f)
        close()
    }
    drawPath(
        path = bellPath,
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFFFFF3B0), Color(0xFFFFD166), Color(0xFFB45309)),
            start = Offset(w * 0.2f, h * 0.2f),
            end = Offset(w * 0.85f, h * 0.75f),
        ),
    )

    // Rim highlight
    drawRoundRect(
        brush = Brush.horizontalGradient(
            listOf(Color(0xFFB45309), Color(0xFFFFE599), Color(0xFFB45309))
        ),
        topLeft = Offset(w * 0.13f, h * 0.68f),
        size = Size(w * 0.74f, h * 0.09f),
        cornerRadius = CornerRadius(w * 0.045f, w * 0.045f),
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawLuckySeven(w: Float, h: Float) {
    val sevenPath = Path().apply {
        moveTo(w * 0.20f, h * 0.16f)
        lineTo(w * 0.82f, h * 0.16f)
        lineTo(w * 0.82f, h * 0.30f)
        lineTo(w * 0.48f, h * 0.86f)
        lineTo(w * 0.29f, h * 0.86f)
        lineTo(w * 0.61f, h * 0.32f)
        lineTo(w * 0.20f, h * 0.32f)
        close()
    }

    // Outer gold trim
    drawPath(
        path = sevenPath,
        color = Color(0xFFFFD166),
        style = Stroke(width = w * 0.10f, join = StrokeJoin.Round),
    )

    // Inner ruby neon fill
    drawPath(
        path = sevenPath,
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFFFF4D79), Color(0xFFE6194B), Color(0xFF9F1239)),
            start = Offset(w * 0.2f, h * 0.15f),
            end = Offset(w * 0.75f, h * 0.85f),
        ),
    )

    // Neon center highlight stroke
    val highlight = Path().apply {
        moveTo(w * 0.28f, h * 0.24f)
        lineTo(w * 0.72f, h * 0.24f)
        lineTo(w * 0.39f, h * 0.79f)
    }
    drawPath(
        path = highlight,
        color = Color.White.copy(alpha = 0.50f),
        style = Stroke(width = w * 0.032f, cap = StrokeCap.Round, join = StrokeJoin.Round),
    )
}
