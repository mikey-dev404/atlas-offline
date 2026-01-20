package com.mikey.atlasoffline

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// ============ MINIMALIST LOGO ============

@Composable
fun MinimalistLogo(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    animate: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition()

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(40000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = this.size.width / 2
            val centerY = this.size.height / 2
            val radius = this.size.minDimension / 2

            // Outer circle
            drawCircle(
                color = Color.White,
                radius = radius * 0.95f,
                center = Offset(centerX, centerY),
                style = Stroke(width = 2f)
            )

            // Inner rotating circles
            for (i in 0..2) {
                val angle = ((rotation + i * 120f) % 360f) * (PI / 180)
                val x = centerX + radius * 0.6f * cos(angle).toFloat()
                val y = centerY + radius * 0.6f * sin(angle).toFloat()

                drawCircle(
                    color = Color.White.copy(alpha = 0.6f),
                    radius = radius * 0.15f,
                    center = Offset(x, y),
                    style = Stroke(width = 1.5f)
                )
            }

            // Center circle
            drawCircle(
                color = Color.White,
                radius = radius * 0.25f,
                center = Offset(centerX, centerY),
                style = Stroke(width = 2f)
            )

            // Center dot
            drawCircle(
                color = Color.White,
                radius = 4f,
                center = Offset(centerX, centerY)
            )
        }
    }
}

// ============ GLASS CARD ============

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.08f),
                        Color.White.copy(alpha = 0.03f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.3f),
                        Color.White.copy(alpha = 0.05f),
                        Color.White.copy(alpha = 0.3f)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        // Top glass shine
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier.padding(20.dp),
            content = content
        )
    }
}

// ============ MINIMALIST BUTTON ============

@Composable
fun MinimalButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    filled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (filled) Color.White else Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (!filled) BorderStroke(1.dp, AtlasColors.Border) else null,
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = if (filled) Color.Black else Color.White
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Text(
                text = text.uppercase(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (filled) Color.Black else Color.White,
                letterSpacing = 1.sp
            )
        }
    }
}

// ============ STAT CARD ============

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.9f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    GlassCard(
        modifier = modifier
            .scale(scale)
            .height(140.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = AtlasColors.TextSecondary
            )

            Column {
                Text(
                    text = value,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = AtlasColors.TextPrimary
                )
                Text(
                    text = title.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = AtlasColors.TextTertiary,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

// ============ PROGRESS RING ============

@Composable
fun ProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    strokeWidth: Dp = 8.dp
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = this.size.width / 2
            val centerY = this.size.height / 2
            val radius = (this.size.minDimension - strokeWidth.toPx()) / 2

            // Background circle
            drawCircle(
                color = Color.White.copy(alpha = 0.1f),
                radius = radius,
                center = Offset(centerX, centerY),
                style = Stroke(width = strokeWidth.toPx())
            )

            // Progress arc
            drawArc(
                color = Color.White,
                startAngle = -90f,
                sweepAngle = progress * 360f,
                useCenter = false,
                topLeft = Offset(centerX - radius, centerY - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}

// ============ MINIMAL BACKGROUND ============

@Composable
fun MinimalBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = AtlasColors.BackgroundGradient
                )
            )
    ) {
        // Subtle grid pattern
        Canvas(modifier = Modifier.fillMaxSize()) {
            val spacing = 100f
            val strokeWidth = 0.5f

            // Vertical lines
            var x = 0f
            while (x < size.width) {
                drawLine(
                    color = Color.White.copy(alpha = 0.03f),
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = strokeWidth
                )
                x += spacing
            }

            // Horizontal lines
            var y = 0f
            while (y < size.height) {
                drawLine(
                    color = Color.White.copy(alpha = 0.03f),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = strokeWidth
                )
                y += spacing
            }
        }

        content()
    }
}

// ============ LEVEL CARD ============

@Composable
fun LevelCard(
    level: Int,
    currentXP: Int,
    maxXP: Int,
    rankTitle: String = "RECRUIT",
    rankSubtitle: String = "Aspiring Warrior",
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ProgressRing(
                    progress = if (maxXP > 0) currentXP.toFloat() / maxXP else 0f,
                    size = 80.dp,
                    strokeWidth = 6.dp
                )

                Column {
                    Text(
                        text = rankTitle,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AtlasColors.TextPrimary,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = rankSubtitle,
                        fontSize = 12.sp,
                        color = AtlasColors.TextSecondary
                    )
                    Text(
                        text = "Level $level • $currentXP / $maxXP XP",
                        fontSize = 11.sp,
                        color = AtlasColors.TextTertiary
                    )
                }
            }
        }
    }
}

