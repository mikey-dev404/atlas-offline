package com.mikey.atlasoffline

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.BorderStroke


@Composable
fun MeasurementsScreen(viewModel: AtlasViewModel) {
    val measurements by viewModel.bodyMeasurements.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    MinimalBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(20.dp)) }

                item {
                    Text(
                        text = "MEASUREMENTS",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 2.sp
                    )
                }

                // Latest measurement summary
                if (measurements.isNotEmpty()) {
                    val latest = measurements.first()

                    item {
                        GlassCard {
                            Text(
                                text = "CURRENT",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(0.6f),
                                letterSpacing = 2.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                latest.weight?.let {
                                    MeasurementItem("Weight", "${it}kg", Modifier.weight(1f))
                                }
                                latest.bodyFat?.let {
                                    MeasurementItem("Body Fat", "$it%", Modifier.weight(1f))
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                latest.chest?.let {
                                    MeasurementItem("Chest", "${it}cm", Modifier.weight(1f))
                                }
                                latest.waist?.let {
                                    MeasurementItem("Waist", "${it}cm", Modifier.weight(1f))
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                latest.biceps?.let {
                                    MeasurementItem("Biceps", "${it}cm", Modifier.weight(1f))
                                }
                                latest.thighs?.let {
                                    MeasurementItem("Thighs", "${it}cm", Modifier.weight(1f))
                                }
                            }
                        }
                    }

                    // Weight progress chart
                    val weightData = measurements
                        .filter { it.weight != null }
                        .takeLast(10)
                        .map { it.timestamp.substringBefore("T") to (it.weight ?: 0f) }

                    if (weightData.size >= 2) {
                        item {
                            WeightProgressChart(
                                data = weightData,
                                title = "Weight Progress"
                            )
                        }
                    }

                    // History
                    item {
                        Text(
                            text = "HISTORY",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(0.6f),
                            letterSpacing = 2.sp
                        )
                    }

                    items(measurements.drop(1).take(10)) { measurement ->
                        GlassCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = measurement.timestamp.substringBefore("T"),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White
                                    )

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        measurement.weight?.let {
                                            Text(
                                                text = "${it}kg",
                                                fontSize = 12.sp,
                                                color = Color.White.copy(0.7f)
                                            )
                                        }
                                        measurement.bodyFat?.let {
                                            Text(
                                                text = "$it% BF",
                                                fontSize = 12.sp,
                                                color = Color.White.copy(0.7f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(100.dp)) }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                MinimalButton(
                    onClick = { showAddDialog = true },
                    text = "Add Measurement",
                    icon = Icons.Default.Add,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    if (showAddDialog) {
        AddMeasurementDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { weight, bodyFat, chest, waist, biceps, thighs ->
                viewModel.addBodyMeasurement(weight, bodyFat, chest, waist, null, biceps, thighs, null)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun MeasurementItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = label.uppercase(),
            fontSize = 10.sp,
            color = Color.White.copy(0.5f),
            letterSpacing = 1.sp
        )
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun AddMeasurementDialog(
    onDismiss: () -> Unit,
    onAdd: (Float?, Float?, Float?, Float?, Float?, Float?) -> Unit
) {
    var weight by remember { mutableStateOf("") }
    var bodyFat by remember { mutableStateOf("") }
    var chest by remember { mutableStateOf("") }
    var waist by remember { mutableStateOf("") }
    var biceps by remember { mutableStateOf("") }
    var thighs by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = AtlasColors.BackgroundLight,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, AtlasColors.Border)
        ) {
            LazyColumn(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ADD MEASUREMENT",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )

                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("Weight (kg)") },
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

                item {
                    OutlinedTextField(
                        value = bodyFat,
                        onValueChange = { bodyFat = it },
                        label = { Text("Body Fat (%)") },
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

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = chest,
                            onValueChange = { chest = it },
                            label = { Text("Chest (cm)") },
                            modifier = Modifier.weight(1f),
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
                            value = waist,
                            onValueChange = { waist = it },
                            label = { Text("Waist (cm)") },
                            modifier = Modifier.weight(1f),
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

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = biceps,
                            onValueChange = { biceps = it },
                            label = { Text("Biceps (cm)") },
                            modifier = Modifier.weight(1f),
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
                            value = thighs,
                            onValueChange = { thighs = it },
                            label = { Text("Thighs (cm)") },
                            modifier = Modifier.weight(1f),
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

                item {
                    MinimalButton(
                        onClick = {
                            onAdd(
                                weight.toFloatOrNull(),
                                bodyFat.toFloatOrNull(),
                                chest.toFloatOrNull(),
                                waist.toFloatOrNull(),
                                biceps.toFloatOrNull(),
                                thighs.toFloatOrNull()
                            )
                        },
                        text = "Save",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
