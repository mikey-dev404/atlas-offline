package com.mikey.atlasoffline

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.graphics.vector.ImageVector









@Composable
fun TemplatesScreen(
    viewModel: AtlasViewModel,
    navController: NavHostController
) {
    val templates by viewModel.templates.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

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
                        text = "TEMPLATES",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 2.sp
                    )
                }

                // -------- TIER 1: BEGINNER FULL BODY / 3-DAY PPL ----------
                item {
                    TierHeader(
                        title = "TIER I • FOUNDATION",
                        subtitle = "Beginner-friendly 3-day full body and PPL splits"
                    )
                }

                item {
                    TierRow(
                        templates = listOf(
                            TierTemplate(
                                name = "Full Body A",
                                tag = "FullBodyA",
                                icon = Icons.Filled.FitnessCenter
                            ),
                            TierTemplate(
                                name = "Full Body B",
                                tag = "FullBodyB",
                                icon = Icons.Filled.Star
                            ),
                            TierTemplate(
                                name = "PPL 3-Day",
                                tag = "PPL3",
                                icon = Icons.Filled.Star
                            ),

                            ),
                        onUse = { tag ->
                            loadTierTemplate(tag, viewModel)
                            navController.navigate("workout")
                        }
                    )
                }

                // -------- TIER 2: INTERMEDIATE PUSH / PULL / LEGS ----------
                item {
                    TierHeader(
                        title = "TIER II • PROGRESSION",
                        subtitle = "Classic push / pull / legs split for hypertrophy"
                    )
                }

                item {
                    TierRow(
                        templates = listOf(
                            TierTemplate(
                                name = "Push Day",
                                tag = "PushDay",
                                icon = Icons.Filled.ArrowUpward
                            ),
                            TierTemplate(
                                name = "Pull Day",
                                tag = "PullDay",
                                icon = Icons.Filled.ArrowDownward
                            ),
                            TierTemplate(
                                name = "Legs Day",
                                tag = "LegDayAdvanced",
                                icon = Icons.Filled.DirectionsRun
                            )
                        ),
                        onUse = { tag ->
                            loadTierTemplate(tag, viewModel)
                            navController.navigate("workout")
                        }
                    )
                }

                // -------- TIER 3: ADVANCED UPPER / LOWER / SPECIALIZATION ----------
                item {
                    TierHeader(
                        title = "TIER III • ADVANCED",
                        subtitle = "Upper / Lower + specialization focus days"
                    )
                }

                item {
                    TierRow(
                        templates = listOf(
                            TierTemplate(
                                name = "Upper Power",
                                tag = "UpperPower",
                                icon = Icons.Filled.Star
                            ),
                            TierTemplate(
                                name = "Lower Power",
                                tag = "LowerPower",
                                icon = Icons.Filled.Star
                            ),
                            TierTemplate(
                                name = "Arms & Delts",
                                tag = "ArmsDelts",
                                icon = Icons.Filled.FitnessCenter
                            )
                        ),
                        onUse = { tag ->
                            loadTierTemplate(tag, viewModel)
                            navController.navigate("workout")
                        }
                    )
                }

                // -------- CUSTOM TEMPLATES ----------
                if (templates.isNotEmpty()) {
                    item {
                        Text(
                            text = "YOUR TEMPLATES",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(0.6f),
                            letterSpacing = 2.sp
                        )
                    }

                    items(templates) { template ->
                        TemplateCard(
                            template = template,
                            onUse = {
                                viewModel.loadTemplate(template)
                                navController.navigate("workout")
                            },
                            onDelete = { viewModel.deleteTemplate(template) }
                        )
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
                    onClick = { showCreateDialog = true },
                    text = "Save Current Workout",
                    icon = Icons.Default.Add,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    if (showCreateDialog) {
        CreateTemplateDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name, description ->
                viewModel.saveCurrentAsTemplate(name, description)
                showCreateDialog = false
            }
        )
    }
}

// ---- Tier helpers ----

data class TierTemplate(
    val name: String,
    val tag: String,
    val icon: ImageVector
)



@Composable
fun TierHeader(
    title: String,
    subtitle: String
) {
    Column {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(0.7f),
            letterSpacing = 2.sp
        )
        Text(
            text = subtitle,
            fontSize = 11.sp,
            color = Color.White.copy(0.6f)
        )
    }
}

@Composable
fun TierRow(
    templates: List<TierTemplate>,
    onUse: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        templates.forEach { t ->
            TierTemplateCard(
                template = t,
                onUse = { onUse(t.tag) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun TierTemplateCard(
    template: TierTemplate,
    onUse: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier
            .height(220.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp, horizontal = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // White icon circle
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = template.icon,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }

            Text(
                text = template.name.uppercase(),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Play button
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable { onUse() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Start template",
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}




// ---- Custom template cards stay mostly the same ----

@Composable
fun TemplateCard(
    template: WorkoutTemplate,
    onUse: () -> Unit,
    onDelete: () -> Unit
) {
    GlassCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = template.name.uppercase(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
                if (template.description.isNotBlank()) {
                    Text(
                        text = template.description,
                        fontSize = 12.sp,
                        color = Color.White.copy(0.6f)
                    )
                }
            }

            Row {
                IconButton(onClick = onUse) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Use", tint = Color.White)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White.copy(0.6f))
                }
            }
        }
    }
}

@Composable
fun CreateTemplateDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("CREATE TEMPLATE", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Template Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { if (name.isNotBlank()) onCreate(name, description) }) {
                Text("CREATE")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL")
            }
        }
    )
}

fun loadTierTemplate(tag: String, viewModel: AtlasViewModel) {
    when (tag) {
        "FullBodyA" -> {
            viewModel.startWorkout()
            listOf(
                "Barbell Squat",
                "Barbell Bench Press",
                "Barbell Row",
                "Overhead Press",
                "Plank"
            ).forEach(viewModel::addExercise)
        }

        "FullBodyB" -> {
            viewModel.startWorkout()
            listOf(
                "Deadlift",
                "Incline Dumbbell Press",
                "Pull-Ups",
                "Walking Lunges",
                "Hanging Leg Raises"
            ).forEach(viewModel::addExercise)
        }

        "PPL3" -> {
            // uses your existing prebuilt loader
            viewModel.loadPrebuiltTemplate("Push Day")
        }

        "PushDay" -> viewModel.loadPrebuiltTemplate("Push Day")
        "PullDay" -> viewModel.loadPrebuiltTemplate("Pull Day")
        "LegDayAdvanced" -> viewModel.loadPrebuiltTemplate("Leg Day")

        "UpperPower" -> {
            viewModel.startWorkout()
            listOf(
                "Barbell Bench Press",
                "Barbell Row",
                "Overhead Press",
                "Weighted Pull-Ups"
            ).forEach(viewModel::addExercise)
        }

        "LowerPower" -> {
            viewModel.startWorkout()
            listOf(
                "Barbell Squat",
                "Deadlift",
                "Bulgarian Split Squat",
                "Calf Raise"
            ).forEach(viewModel::addExercise)
        }

        "ArmsDelts" -> {
            viewModel.startWorkout()
            listOf(
                "Barbell Curl",
                "Hammer Curl",
                "Skull Crushers",
                "Overhead Tricep Extension",
                "Lateral Raise",
                "Rear Delt Fly"
            ).forEach(viewModel::addExercise)
        }
    }
}
