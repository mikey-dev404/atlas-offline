package com.mikey.atlasoffline

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ============ LINE CHART ============

@Composable
fun WeightProgressChart(
    data: List<Pair<String, Float>>,
    modifier: Modifier = Modifier,
    title: String = "Weight Progress"
) {
    if (data.isEmpty()) {
        EmptyChartState(modifier, "No data to display")
        return
    }

    GlassCard(modifier = modifier) {
        Text(
            text = title.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = AtlasColors.TextSecondary,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            if (data.size < 2) return@Canvas

            val maxValue = data.maxOf { it.second }
            val minValue = data.minOf { it.second }
            val range = maxValue - minValue

            val stepX = size.width / (data.size - 1)
            val stepY = size.height / (range + 1)

            val path = Path()

            // Draw grid
            for (i in 0..4) {
                val y = size.height - (i * size.height / 4)
                drawLine(
                    color = Color.White.copy(alpha = 0.1f),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1f
                )
            }

            // Build path
            data.forEachIndexed { index, (_, value) ->
                val x = index * stepX
                val y = size.height - ((value - minValue) * stepY)

                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }

            // Draw gradient fill
            val fillPath = Path().apply {
                addPath(path)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.2f),
                        Color.Transparent
                    )
                )
            )

            // Draw line
            drawPath(
                path = path,
                color = Color.White,
                style = Stroke(width = 3f, cap = StrokeCap.Round)
            )

            // Draw points
            data.forEachIndexed { index, (_, value) ->
                val x = index * stepX
                val y = size.height - ((value - minValue) * stepY)

                drawCircle(
                    color = Color.White,
                    radius = 5f,
                    center = Offset(x, y)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = data.first().first,
                fontSize = 11.sp,
                color = AtlasColors.TextMuted
            )
            Text(
                text = data.last().first,
                fontSize = 11.sp,
                color = AtlasColors.TextMuted
            )
        }
    }
}

// ============ BAR CHART ============

@Composable
fun WeeklyWorkoutChart(
    data: Map<String, Int>,
    modifier: Modifier = Modifier,
    title: String = "Weekly Activity"
) {
    GlassCard(modifier = modifier) {
        Text(
            text = title.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = AtlasColors.TextSecondary,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            val maxValue = data.values.maxOrNull() ?: 1
            val barWidth = size.width / (data.size * 2)

            data.entries.forEachIndexed { index, (_, count) ->
                val barHeight = (count.toFloat() / maxValue) * size.height
                val x = index * barWidth * 2 + barWidth / 2
                val y = size.height - barHeight

                drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            data.keys.forEach { day ->
                Text(
                    text = day.take(3),
                    fontSize = 11.sp,
                    color = AtlasColors.TextMuted
                )
            }
        }
    }
}

// ============ PIE CHART ============

@Composable
fun MuscleGroupPieChart(
    data: Map<String, Int>,
    modifier: Modifier = Modifier,
    title: String = "Muscle Focus"
) {
    GlassCard(modifier = modifier) {
        Text(
            text = title.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = AtlasColors.TextSecondary,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Canvas(modifier = Modifier.size(140.dp)) {
                val total = data.values.sum().toFloat()
                var startAngle = -90f

                val grays = listOf(
                    Color.White,
                    Color(0xFFCCCCCC),
                    Color(0xFF999999),
                    Color(0xFF666666),
                    Color(0xFFE5E5E5),
                    Color(0xFFB0B0B0),
                    Color(0xFF808080)
                )

                data.values.forEachIndexed { index, count ->
                    val sweepAngle = (count / total) * 360f

                    drawArc(
                        color = grays[index % grays.size],
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        topLeft = Offset(10f, 10f),
                        size = Size(size.width - 20f, size.height - 20f)
                    )

                    startAngle += sweepAngle
                }

                drawCircle(
                    color = AtlasColors.Background,
                    radius = size.width / 4,
                    center = Offset(size.width / 2, size.height / 2)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                data.entries.forEachIndexed { index, (group, count) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 1f - (index * 0.15f)))
                        )

                        Column {
                            Text(
                                text = group,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = AtlasColors.TextPrimary
                            )
                            Text(
                                text = "$count exercises",
                                fontSize = 10.sp,
                                color = AtlasColors.TextTertiary
                            )
                        }
                    }
                }
            }
        }
    }
}

// ============ VOLUME CHART ============

@Composable
fun VolumeChart(
    data: List<Pair<String, Float>>,
    modifier: Modifier = Modifier,
    title: String = "Training Volume"
) {
    if (data.isEmpty()) {
        EmptyChartState(modifier, "No volume data")
        return
    }

    GlassCard(modifier = modifier) {
        Text(
            text = title.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = AtlasColors.TextSecondary,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            val maxValue = data.maxOf { it.second }
            val barWidth = size.width / (data.size * 1.5f)

            data.forEachIndexed { index, (_, volume) ->
                val barHeight = (volume / maxValue) * size.height
                val x = index * barWidth * 1.5f
                val y = size.height - barHeight

                drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Total: ${data.sumOf { it.second.toDouble() }.toInt()} kg",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = AtlasColors.TextPrimary
        )
    }
}

// ============ EMPTY STATE ============

@Composable
fun EmptyChartState(
    modifier: Modifier = Modifier,
    message: String = "No data available"
) {
    GlassCard(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "📊",
                    fontSize = 40.sp
                )
                Text(
                    text = message,
                    fontSize = 13.sp,
                    color = AtlasColors.TextSecondary
                )
            }
        }
    }
}
