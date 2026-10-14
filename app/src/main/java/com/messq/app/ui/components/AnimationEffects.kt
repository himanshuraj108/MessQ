package com.messq.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ripple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.random.Random

fun Modifier.shimmerEffect(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmerTransition")
    val translateAnim by transition.animateFloat(
        initialValue = -500f,
        targetValue = 1500f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val shimmerColors = listOf(
        Color(0xFFF1F5F9),
        Color(0xFFFFFFFF),
        Color(0xFFE2E8F0),
        Color(0xFFFFFFFF),
        Color(0xFFF1F5F9)
    )

    this.drawBehind {
        val brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(translateAnim, translateAnim),
            end = Offset(translateAnim + 400f, translateAnim + 400f)
        )
        drawRect(brush = brush)
    }
}

fun Modifier.holographicSweep(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "holoTransition")
    val progress by transition.animateFloat(
        initialValue = -0.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "holoProgress"
    )

    this.drawBehind {
        val width = size.width
        val height = size.height
        val startX = width * progress
        val brush = Brush.linearGradient(
            colors = listOf(
                Color.Transparent,
                Color.White.copy(alpha = 0.05f),
                Color(0xFFFFD700).copy(alpha = 0.18f),
                Color(0xFF38BDF8).copy(alpha = 0.22f),
                Color(0xFFF472B6).copy(alpha = 0.18f),
                Color.White.copy(alpha = 0.25f),
                Color.Transparent
            ),
            start = Offset(startX, 0f),
            end = Offset(startX + width * 0.45f, height)
        )
        drawRect(brush = brush)
    }
}

fun Modifier.bouncyClick(
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "bouncyScale"
    )

    this
        .scale(scale)
        .clickable(
            interactionSource = interactionSource,
            indication = ripple(color = Color(0xFFF97316).copy(alpha = 0.20f)),
            enabled = enabled,
            onClick = onClick
        )
}

fun Modifier.breathingGlow(color: Color = Color(0xFFF97316)): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "glowTransition")
    val alpha by transition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )
    val spread by transition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowSpread"
    )

    this.drawBehind {
        drawCircle(
            color = color.copy(alpha = alpha),
            radius = (size.maxDimension / 2f) * spread
        )
    }
}

@Composable
fun PulsingRadarDot(
    color: Color = Color(0xFF16A34A),
    dotSize: Dp = 8.dp,
    ringSize: Dp = 22.dp,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "radarTransition")
    val radiusRatio by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radarRadius"
    )
    val ringAlpha by transition.animateFloat(
        initialValue = 0.7f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radarAlpha"
    )

    Box(
        modifier = modifier.size(ringSize),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(ringSize)) {
            val maxR = size.minDimension / 2f
            drawCircle(
                color = color.copy(alpha = ringAlpha),
                radius = maxR * radiusRatio,
                style = Stroke(width = 1.8.dp.toPx())
            )
            drawCircle(
                color = color,
                radius = dotSize.toPx() / 2f
            )
        }
    }
}

private data class ConfettiParticle(
    val startX: Float,
    val speedY: Float,
    val speedX: Float,
    val size: Float,
    val color: Color,
    val rotationSpeed: Float,
    val isCircle: Boolean
)

@Composable
fun ConfettiCanvas(
    modifier: Modifier = Modifier
) {
    val particles = remember {
        val colors = listOf(
            Color(0xFFF97316),
            Color(0xFFE85D00),
            Color(0xFF10B981),
            Color(0xFF3B82F6),
            Color(0xFFF59E0B),
            Color(0xFFEC4899),
            Color(0xFF8B5CF6)
        )
        val rnd = Random(42)
        List(45) {
            ConfettiParticle(
                startX = rnd.nextFloat(),
                speedY = rnd.nextFloat() * 220f + 160f,
                speedX = (rnd.nextFloat() - 0.5f) * 80f,
                size = rnd.nextFloat() * 10f + 6f,
                color = colors[rnd.nextInt(colors.size)],
                rotationSpeed = (rnd.nextFloat() - 0.5f) * 360f,
                isCircle = rnd.nextBoolean()
            )
        }
    }

    val transition = rememberInfiniteTransition(label = "confettiTransition")
    val time by transition.animateFloat(
        initialValue = 0f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "confettiTime"
    )

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        particles.forEach { p ->
            val totalY = (p.speedY * time) % (canvasHeight + 100f) - 50f
            val totalX = (p.startX * canvasWidth + p.speedX * time) % canvasWidth
            val alpha = (1f - (totalY / canvasHeight).coerceIn(0f, 1f)).coerceIn(0.2f, 1f)

            if (p.isCircle) {
                drawCircle(
                    color = p.color.copy(alpha = alpha),
                    radius = p.size / 2f,
                    center = Offset(totalX, totalY)
                )
            } else {
                drawRect(
                    color = p.color.copy(alpha = alpha),
                    topLeft = Offset(totalX, totalY),
                    size = androidx.compose.ui.geometry.Size(p.size, p.size * 1.5f)
                )
            }
        }
    }
}

@Composable
fun ConcentricShockwave(
    color: Color = Color(0xFFF97316),
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "shockwaveTransition")
    val wave1 by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave1"
    )
    val wave2 by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, delayMillis = 400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave2"
    )

    Canvas(modifier = modifier) {
        val baseRadius = size.minDimension / 2f
        val alpha1 = (1f - (wave1 - 0.2f) / 1.4f).coerceIn(0f, 0.6f)
        val alpha2 = (1f - (wave2 - 0.2f) / 1.4f).coerceIn(0f, 0.6f)

        drawCircle(
            color = color.copy(alpha = alpha1),
            radius = baseRadius * wave1,
            style = Stroke(width = 2.5.dp.toPx())
        )
        drawCircle(
            color = color.copy(alpha = alpha2),
            radius = baseRadius * wave2,
            style = Stroke(width = 2.dp.toPx())
        )
    }
}
