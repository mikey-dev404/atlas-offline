package com.mikey.atlasoffline

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun CardioScreen(
    viewModel: AtlasViewModel,
    navController: NavHostController
) {
    val sessions by viewModel.cardioSessions.collectAsState()

    var type by remember { mutableStateOf("Running") }
    var duration by remember { mutableStateOf("") }
    var distance by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Cardio", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(16.dp))

        // Simple type selector – can be upgraded later
        val types = listOf("Running", "Walking", "Cycling", "Rowing", "Elliptical", "Other")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            types.forEach { t ->
                FilterChip(
                    selected = type == t,
                    onClick = { type = t },
                    label = { Text(t) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = duration,
            onValueChange = { input -> duration = input.filter(Char::isDigit) },
            label = { Text("Duration (min)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = distance,
            onValueChange = { input ->
                distance = input.filter { it.isDigit() || it == '.' }
            },
            label = { Text("Distance (km, optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                val dur = duration.toIntOrNull() ?: 0
                val dist = distance.toFloatOrNull() ?: 0f
                if (dur > 0) {
                    viewModel.addCardioSession(
                        type = type,
                        duration = dur,
                        distance = dist
                    )
                    duration = ""
                    distance = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Cardio Session")
        }

        Spacer(Modifier.height(24.dp))

        Text("Recent Cardio", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        if (sessions.isEmpty()) {
            Text("No cardio sessions yet.")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(sessions) { s ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text("${s.type} - ${s.duration} min")
                            if (s.distance > 0f) {
                                Text("${s.distance} km")
                            }
                        }
                    }
                }
            }
        }
    }
}
