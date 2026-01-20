package com.mikey.atlasoffline

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PlateCalculatorScreen() {
    var targetWeight by remember { mutableStateOf("") }
    var barWeight by remember { mutableStateOf("20") }
    var unit by remember { mutableStateOf("kg") }

    val availablePlates = if (unit == "kg") {
        listOf(25f, 20f, 15f, 10f, 5f, 2.5f, 1.25f)
    } else {
        listOf(45f, 35f, 25f, 10f, 5f, 2.5f)
    }

    val plateConfig = remember(targetWeight, barWeight) {
        calculatePlates(
            targetWeight.toFloatOrNull() ?: 0f,
            barWeight.toFloatOrNull() ?: 20f,
            availablePlates
        )
    }

    MinimalBackground {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(20.dp)) }

            item {
                Text(
                    text = "PLATE CALCULATOR",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 2.sp
                )
            }

            item {
                GlassCard {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Unit toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            FilterChip(
                                selected = unit == "kg",
                                onClick = { unit = "kg"; barWeight = "20" },
                                label = { Text("KG") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color.White.copy(0.2f),
                                    selectedLabelColor = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            FilterChip(
                                selected = unit == "lbs",
                                onClick = { unit = "lbs"; barWeight = "45" },
                                label = { Text("LBS") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color.White.copy(0.2f),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }

                        OutlinedTextField(
                            value = targetWeight,
                            onValueChange = { targetWeight = it },
                            label = { Text("Target Weight") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(Icons.Default.FitnessCenter, contentDescription = null)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.White.copy(0.3f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color.White,
                                focusedLabelColor = Color.White,
                                unfocusedLabelColor = Color.White.copy(0.6f)
                            )
                        )

                        OutlinedTextField(
                            value = barWeight,
                            onValueChange = { barWeight = it },
                            label = { Text("Bar Weight") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.White.copy(0.3f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color.White,
                                focusedLabelColor = Color.White,
                                unfocusedLabelColor = Color.White.copy(0.6f)
                            )
                        )
                    }
                }
            }

            if (plateConfig.isNotEmpty()) {
                item {
                    GlassCard {
                        Column {
                            Text(
                                text = "LOAD EACH SIDE:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(0.6f),
                                letterSpacing = 2.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            plateConfig.forEach { (weight, count) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .background(Color.White.copy(0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "$count×",
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }

                                        Text(
                                            text = "${weight}$unit plate",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Divider(color = Color.White.copy(0.2f))

                            Spacer(modifier = Modifier.height(16.dp))

                            val totalLoaded = plateConfig.sumOf { (weight, count) ->
                                (weight * count * 2).toDouble()
                            }.toFloat() + (barWeight.toFloatOrNull() ?: 0f)

                            Text(
                                text = "Total: ${totalLoaded}$unit",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

fun calculatePlates(
    targetWeight: Float,
    barWeight: Float,
    availablePlates: List<Float>
): List<Pair<Float, Int>> {
    if (targetWeight <= barWeight) return emptyList()

    var remainingWeight = (targetWeight - barWeight) / 2
    val result = mutableListOf<Pair<Float, Int>>()

    for (plateWeight in availablePlates.sortedDescending()) {
        val count = (remainingWeight / plateWeight).toInt()
        if (count > 0) {
            result.add(plateWeight to count)
            remainingWeight -= plateWeight * count
        }
    }

    return result
}
