package com.mikey.atlasoffline

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RestTimerOverlay(
    timeRemaining: Int,
    exerciseName: String,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        GlassCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(AtlasColors.GlassMedium),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$timeRemaining",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.scale(pulse)
                        )
                    }

                    Column {
                        Text(
                            text = "REST TIME",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AtlasColors.TextSecondary,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = exerciseName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = AtlasColors.TextPrimary
                        )
                    }
                }

                IconButton(
                    onClick = onSkip,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(AtlasColors.GlassDark)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Skip",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(AtlasColors.GlassDark)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth((timeRemaining / 90f).coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .background(Color.White, CircleShape)
                )
            }
        }
    }
}
