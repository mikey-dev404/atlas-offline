package com.mikey.atlasoffline

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource



// ============ SPLASH SCREEN ============

@Composable
fun SplashScreen(onEnter: () -> Unit) {
    var phase by remember { mutableStateOf(0) }

    // Floating animation for David
    val infiniteTransition = rememberInfiniteTransition(label = "david_float")
    val davidOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -16f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "david_offset"
    )

    // Simple progress value for bottom bar
    val progress by animateFloatAsState(
        targetValue = if (phase >= 3) 1f else when (phase) {
            0 -> 0f
            1 -> 0.4f
            2 -> 0.7f
            else -> 1f
        },
        animationSpec = tween(900),
        label = "splash_progress"
    )

    LaunchedEffect(Unit) {
        delay(300)
        phase = 1
        delay(800)
        phase = 2
        delay(600)
        phase = 3
    }

    MinimalBackground {
        Box(modifier = Modifier.fillMaxSize()) {

            // David – bigger (about 80% of height), floating
            Image(
                painter = painterResource(id = R.drawable.david),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
                    .align(Alignment.BottomCenter)
                    .offset(y = davidOffset.dp)
                    .alpha(0.18f)
                    .graphicsLayer {
                        shadowElevation = 26f
                        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
                        clip = true
                    },
                contentScale = ContentScale.Crop
            )

            // Foreground
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp, vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top logo + title
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AnimatedVisibility(
                        visible = phase >= 1,
                        enter = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn()
                    ) {
                        Box(
                            modifier = Modifier.graphicsLayer {
                                shadowElevation = 24f
                            }
                        ) {
                            MinimalistLogo(size = 110.dp)
                        }
                    }

                    AnimatedVisibility(
                        visible = phase >= 2,
                        enter = fadeIn(animationSpec = tween(500, delayMillis = 150))
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            // Main wordmark – use your Greek/modern font via typography if you add it later
                            Text(
                                text = "ATLAS",
                                fontSize = 42.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                letterSpacing = 8.sp
                            )
                            Text(
                                text = "OFFLINE",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.6f),
                                letterSpacing = 8.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Minimalist Strength Tracker",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.55f)
                            )
                        }
                    }
                }

                // Bottom: progress bar + button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Replace spinner with sleek progress bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(Color.White.copy(alpha = 0.10f))
                            .graphicsLayer { shadowElevation = 14f }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(progress.coerceIn(0f, 1f))
                                .clip(RoundedCornerShape(999.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color.White,
                                            Color.White.copy(alpha = 0.4f)
                                        )
                                    )
                                )
                        )
                    }

                    AnimatedVisibility(
                        visible = phase >= 3,
                        enter = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn()
                    ) {
                        Box(
                            modifier = Modifier.graphicsLayer {
                                shadowElevation = 26f
                            }
                        ) {
                            Button(
                                onClick = onEnter,
                                modifier = Modifier
                                    .width(220.dp)
                                    .height(52.dp),
                                shape = RoundedCornerShape(999.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White.copy(alpha = 0.08f),
                                    contentColor = Color.White
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    Color.White.copy(alpha = 0.35f)
                                ),
                                contentPadding = PaddingValues(horizontal = 20.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = Color.White
                                    )
                                    Text(
                                        text = "ENTER ATLAS",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 3.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}




// ============ HOME SCREEN ============

@Composable
fun HomeScreen(
    viewModel: AtlasViewModel,
    navController: NavHostController
) {
    val stats by viewModel.stats.collectAsState()
    val recentExercises by viewModel.recentExercises.collectAsState()
    val restTimerActive by viewModel.restTimerActive.collectAsState()
    val restTimeRemaining by viewModel.restTimeRemaining.collectAsState()
    val currentExercise by viewModel.currentExerciseForTimer.collectAsState()

    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

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
                    AnimatedVisibility(
                        visible = visible,
                        enter = slideInVertically(initialOffsetY = { -50 }) + fadeIn()
                    ) {
                        Column {
                            Text(
                                text = "ATLAS",
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Black,
                                color = AtlasColors.TextPrimary,
                                letterSpacing = 4.sp
                            )
                            Text(
                                text = "Minimalist Fitness Tracker",
                                fontSize = 13.sp,
                                color = AtlasColors.TextTertiary,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }

                item {
                    LevelCard(
                        level = stats.level,
                        currentXP = stats.currentXP % stats.nextLevelXP,
                        maxXP = stats.nextLevelXP,
                        rankTitle = stats.rankTitle,
                        rankSubtitle = stats.rankSubtitle
                    )
                }


                item {
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(animationSpec = tween(600, delayMillis = 200))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCard(
                                title = "Workouts",
                                value = stats.totalWorkouts.toString(),
                                icon = Icons.Default.FitnessCenter,
                                modifier = Modifier.weight(1f)
                            )

                            StatCard(
                                title = "Records",
                                value = stats.personalRecords.toString(),
                                icon = Icons.Default.EmojiEvents,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                item {
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(animationSpec = tween(600, delayMillis = 400))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCard(
                                title = "Time",
                                value = "${stats.totalTime}m",
                                icon = Icons.Default.Timer,
                                modifier = Modifier.weight(1f)
                            )

                            StatCard(
                                title = "Volume",
                                value = "${stats.totalVolume.toInt()}kg",
                                icon = Icons.AutoMirrored.Filled.TrendingUp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "RECENT ACTIVITY",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AtlasColors.TextSecondary,
                        letterSpacing = 2.sp
                    )
                }

                items(recentExercises.take(5)) { exercise ->
                    MinimalExerciseCard(exercise)
                }

                item { Spacer(modifier = Modifier.height(100.dp)) }
            }

            if (restTimerActive && currentExercise != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 80.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    RestTimerOverlay(
                        timeRemaining = restTimeRemaining,
                        exerciseName = currentExercise ?: "",
                        onSkip = { viewModel.stopRestTimer() }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                AnimatedVisibility(
                    visible = visible,
                    enter = slideInVertically(
                        initialOffsetY = { 200 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                    )
                ) {
                    MinimalButton(
                        onClick = {
                            viewModel.startWorkout()
                            navController.navigate("workout")
                        },
                        text = "Start Workout",
                        icon = Icons.Default.Add,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun LevelCard(
    level: Int,
    currentXP: Int,
    maxXP: Int,
    rankTitle: String,
    rankSubtitle: String
) {
    val imageRes = RankArt.imageForRankTitle(rankTitle)
    val progress = if (maxXP > 0) (currentXP.toFloat() / maxXP).coerceIn(0f, 1f) else 0f

    GlassCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank image circle
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
            ) {
                imageRes?.let { resId ->
                    Image(
                        painter = painterResource(id = resId),
                        contentDescription = rankTitle,
                        modifier = Modifier
                            .matchParentSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.3f),
                                    Color.Black.copy(alpha = 0.8f)
                                )
                            )
                        )
                        .border(3.dp, Color.White.copy(alpha = 0.7f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "LVL $level",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Text + bar
            Column(
                modifier = Modifier.weight(1f)
            ) {
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

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.15f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Level $level • $currentXP / $maxXP XP",
                    fontSize = 11.sp,
                    color = AtlasColors.TextTertiary
                )
            }
        }
    }
}


@Composable
fun MinimalExerciseCard(exercise: ExerciseEntry) {
    GlassCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = exercise.exerciseName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = AtlasColors.TextPrimary
                )
                Text(
                    text = "${exercise.sets} sets • ${exercise.weights.split(",").first()}kg",
                    fontSize = 12.sp,
                    color = AtlasColors.TextTertiary
                )
            }

            Text(
                text = exercise.timestamp.substringBefore("T"),
                fontSize = 11.sp,
                color = AtlasColors.TextMuted
            )
        }
    }
}

// ============ WORKOUT SCREEN ============

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen(
    viewModel: AtlasViewModel,
    navController: NavHostController
) {
    val activeExercises by viewModel.activeExercises.collectAsState()
    val startTime by viewModel.workoutStartTime.collectAsState()
    val restTimerActive by viewModel.restTimerActive.collectAsState()
    val restTimeRemaining by viewModel.restTimeRemaining.collectAsState()
    val currentExercise by viewModel.currentExerciseForTimer.collectAsState()

    var showExercisePicker by remember { mutableStateOf(false) }
    var elapsedTime by remember { mutableStateOf(0L) }

    LaunchedEffect(startTime) {
        while (startTime != null) {
            elapsedTime = ChronoUnit.MINUTES.between(startTime, LocalDateTime.now())
            delay(60000)
        }
    }

    MinimalBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                "ACTIVE WORKOUT",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                letterSpacing = 1.sp
                            )
                            Text(
                                "${elapsedTime}min elapsed",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    },
                    actions = {
                        TextButton(
                            onClick = {
                                viewModel.finishWorkout()
                                navController.navigateUp()
                            }
                        ) {
                            Text(
                                "FINISH",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                if (activeExercises.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "⚡",
                            fontSize = 64.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Ready to Start",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Add exercises to begin",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Column {
                        if (restTimerActive && currentExercise != null) {
                            RestTimerOverlay(
                                timeRemaining = restTimeRemaining,
                                exerciseName = currentExercise ?: "",
                                onSkip = { viewModel.stopRestTimer() }
                            )
                        }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item { Spacer(modifier = Modifier.height(8.dp)) }

                            itemsIndexed(activeExercises) { index, exercise ->
                                WorkoutExerciseCard(
                                    exercise = exercise,
                                    exerciseNumber = index + 1,
                                    onAddSet = { reps, weight ->
                                        viewModel.addSet(index, reps, weight)
                                    },
                                    onRemoveSet = { setIndex ->
                                        viewModel.removeSet(index, setIndex)
                                    },
                                    onRemove = {
                                        viewModel.removeExercise(index)
                                    }
                                )
                            }

                            item { Spacer(modifier = Modifier.height(100.dp)) }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    FloatingActionButton(
                        onClick = { showExercisePicker = true },
                        containerColor = Color.White,
                        modifier = Modifier.size(60.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Exercise",
                            tint = Color.Black,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }

    if (showExercisePicker) {
        ExercisePickerDialog(
            onDismiss = { showExercisePicker = false },
            onSelect = { exerciseName ->
                viewModel.addExercise(exerciseName)
                showExercisePicker = false
                showExercisePicker = false
            }
        )
    }
}

@Composable
fun WorkoutExerciseCard(
    exercise: ActiveExercise,
    exerciseNumber: Int,
    onAddSet: (Int, Float) -> Unit,
    onRemoveSet: (Int) -> Unit,
    onRemove: () -> Unit
) {
    val context = LocalContext.current // ADD THIS LINE
    var reps by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }



    GlassCard {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$exerciseNumber",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Column {
                        Text(
                            text = exercise.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${exercise.sets.size} sets",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }

                IconButton(
                    onClick = {
                        val r = reps.toIntOrNull() ?: 0
                        val w = weight.toFloatOrNull() ?: 0f
                        if (r > 0 && w > 0) {
                            onAddSet(r, w)
                            reps = ""
                            weight = ""
                            HapticFeedback.success(context) // ADD THIS LINE
                        }
                    },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Set",
                        tint = Color.Black
                    )
                }

            }

            if (exercise.sets.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                exercise.sets.forEachIndexed { index, set ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Set ${index + 1}: ${set.reps} × ${set.weight}kg",
                            fontSize = 14.sp,
                            color = Color.White
                        )

                        IconButton(
                            onClick = { onRemoveSet(index) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Remove Set",
                                tint = Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = reps,
                    onValueChange = { reps = it },
                    label = { Text("Reps") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
                        cursorColor = Color.White
                    )
                )

                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("kg") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
                        cursorColor = Color.White
                    )
                )

                IconButton(
                    onClick = {
                        val r = reps.toIntOrNull() ?: 0
                        val w = weight.toFloatOrNull() ?: 0f
                        if (r > 0 && w > 0) {
                            onAddSet(r, w)
                            reps = ""
                            weight = ""
                        }
                    },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Set",
                        tint = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun ExercisePickerDialog(
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredExercises = remember(searchQuery) {
        if (searchQuery.isEmpty()) {
            ExerciseDatabase.exercises
        } else {
            ExerciseDatabase.exercises.filter { it.contains(searchQuery, ignoreCase = true) }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            color = AtlasColors.BackgroundLight,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, AtlasColors.Border)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SELECT EXERCISE",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search exercises...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredExercises) { exercise ->
                        Card(
                            onClick = { onSelect(exercise) },
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.05f)
                            )
                        ) {
                            Text(
                                text = exercise,
                                modifier = Modifier.padding(16.dp),
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// ============ STATS SCREEN ============

@Composable
fun StatsScreen(viewModel: AtlasViewModel) {
    val stats by viewModel.stats.collectAsState()
    val chartData by viewModel.chartData.collectAsState()
    var showRanksDialog by remember { mutableStateOf(false) }

    MinimalBackground {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(20.dp)) }

            item {
                GlassCard {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stats.rankTitle,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = 4.sp
                        )

                        Text(
                            text = stats.rankSubtitle,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White.copy(0.6f),
                            letterSpacing = 2.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Current rank art + glass level
                        CurrentRankTile(
                            rankTitle = stats.rankTitle,
                            level = stats.level,
                            onClick = { showRanksDialog = true }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${stats.currentXP} XP",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )

                                Text(
                                    text = if (stats.level < 100) "${stats.nextLevelXP} XP" else "MAX",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(0.6f)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.White.copy(0.1f))
                            ) {
                                val progress = if (stats.nextLevelXP > 0) {
                                    (stats.currentXP.toFloat() / stats.nextLevelXP).coerceIn(0f, 1f)
                                } else 1f

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(progress)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            Brush.horizontalGradient(
                                                colors = listOf(
                                                    Color.White,
                                                    Color.White.copy(0.7f)
                                                )
                                            )
                                        )
                                )
                            }

                            if (stats.level < 100) {
                                val nextRankInfo = GreekRanks.getNextRank(stats.level)
                                nextRankInfo?.let { rank ->
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Next: ${rank.title}",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(0.5f),
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "CAREER STATS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(0.6f),
                    letterSpacing = 2.sp
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GamingStatCard(
                        icon = {
                            Image(
                                painter = painterResource(id = R.drawable.ic_workout),
                                contentDescription = "Workouts",
                                modifier = Modifier.size(32.dp),
                                contentScale = ContentScale.Fit
                            )
                        },
                        value = "${stats.totalWorkouts}",
                        label = "WORKOUTS",
                        modifier = Modifier.weight(1f)
                    )


                    GamingStatCard(
                        icon = {
                            Image(
                                painter = painterResource(id = R.drawable.ic_stopwatch),
                                contentDescription = "Minutes",
                                modifier = Modifier.size(32.dp),
                                contentScale = ContentScale.Fit
                            )
                        },
                        value = "${stats.totalTime}",
                        label = "MINUTES",
                        modifier = Modifier.weight(1f)
                    )

                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GamingStatCard(
                        icon = {
                            Image(
                                painter = painterResource(id = R.drawable.ic_weight),
                                contentDescription = "KG lifted",
                                modifier = Modifier.size(32.dp),
                                contentScale = ContentScale.Fit
                            )
                        },
                        value = "${stats.totalVolume.toInt()}",
                        label = "KG LIFTED",
                        modifier = Modifier.weight(1f)
                    )


                    GamingStatCard(
                        icon = {
                            Image(
                                painter = painterResource(R.drawable.ic_records),
                                contentDescription = "Records",
                                modifier = Modifier.size(32.dp),
                                contentScale = ContentScale.Fit
                            )
                        },
                        value = "${stats.personalRecords}",
                        label = "RECORDS",
                        modifier = Modifier.weight(1f)
                    )

                }
            }

            if (chartData.weeklyWorkouts.values.sum() > 0) {
                item {
                    Text(
                        text = "WEEKLY ACTIVITY",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(0.6f),
                        letterSpacing = 2.sp
                    )
                }

                item {
                    WeeklyWorkoutChart(data = chartData.weeklyWorkouts)
                }
            }

            if (chartData.volumeProgress.isNotEmpty()) {
                item {
                    Text(
                        text = "VOLUME PROGRESS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(0.6f),
                        letterSpacing = 2.sp
                    )
                }

                item {
                    VolumeChart(data = chartData.volumeProgress)
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }

    if (showRanksDialog) {
        RankListDialog(
            currentLevel = stats.level,
            currentXP = stats.currentXP,
            onDismiss = { showRanksDialog = false }
        )
    }
}

@Composable
fun CurrentRankTile(
    rankTitle: String,
    level: Int,
    onClick: () -> Unit
) {
    val currentRankImageRes = RankArt.imageForRankTitle(rankTitle)

    Box(
        modifier = Modifier
            .size(120.dp) // bigger than before
            .clip(CircleShape)
            .clickable { onClick() }
    ) {
        currentRankImageRes?.let { resId ->
            Image(
                painter = painterResource(id = resId),
                contentDescription = rankTitle,
                modifier = Modifier
                    .matchParentSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.25f),
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
                .border(4.dp, Color.White.copy(0.9f), CircleShape)
                .graphicsLayer(
                    scaleX = 1.05f,
                    scaleY = 1.05f,
                    shadowElevation = 16f
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "LEVEL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(0.7f),
                    letterSpacing = 1.sp
                )
                Text(
                    text = "$level",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun RankListDialog(
    currentLevel: Int,
    currentXP: Int,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = AtlasColors.BackgroundLight,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, AtlasColors.Border)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "RANKS",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 2.sp
                )

                Text(
                    text = "Current XP: $currentXP",
                    fontSize = 12.sp,
                    color = Color.White.copy(0.7f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                ) {
                    items(GreekRanks.ranks) { rank: GreekRanks.Rank ->
                        val isCurrent = currentLevel >= rank.level &&
                                (GreekRanks.getNextRank(rank.level)?.level?.let { nextLvl ->
                                    currentLevel < nextLvl
                                } ?: true)

                        RankRow(rank = rank, isCurrent = isCurrent)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                MinimalButton(
                    onClick = onDismiss,
                    text = "Close",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun RankRow(
    rank: GreekRanks.Rank,
    isCurrent: Boolean
) {
    val bg = if (isCurrent) Color.White.copy(0.16f) else Color.White.copy(0.03f)
    val border = if (isCurrent) Color.White.copy(0.9f) else Color.White.copy(0.15f)
    val imageRes = RankArt.imageForRankTitle(rank.title)

    val scale = if (isCurrent) 1.03f else 1f
    val alpha = if (isCurrent) 1f else 0.7f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = if (isCurrent) 6.dp else 4.dp)
            .clip(RoundedCornerShape(if (isCurrent) 14.dp else 12.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(if (isCurrent) 14.dp else 12.dp))
            .padding(horizontal = 12.dp, vertical = if (isCurrent) 10.dp else 8.dp)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                shadowElevation = if (isCurrent) 12f else 0f
            )
            .alpha(alpha),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(if (isCurrent) 48.dp else 40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White.copy(alpha = if (isCurrent) 0.14f else 0.08f))
        ) {
            imageRes?.let { resId ->
                Image(
                    painter = painterResource(id = resId),
                    contentDescription = rank.title,
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "LVL ${rank.level} • ${rank.title}",
                fontSize = if (isCurrent) 14.sp else 13.sp,
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                color = Color.White
            )
            Text(
                text = rank.subtitle,
                fontSize = 11.sp,
                color = Color.White.copy(0.7f)
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${rank.xpRequired} XP",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(0.9f)
            )
            if (isCurrent) {
                Text(
                    text = "CURRENT",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(0.9f)
                )
            }
        }
    }
}



@Composable
fun GamingStatCard(
    icon: @Composable () -> Unit,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Draw the passed composable icon
            icon()

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )

            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(0.5f),
                letterSpacing = 1.sp
            )
        }
    }
}



// ============ CARDIO SCREEN ============

@Composable
fun CardioScreen(viewModel: AtlasViewModel) {
    val cardioSessions by viewModel.cardioSessions.collectAsState()
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
                        text = "CARDIO",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 2.sp
                    )
                }

                if (cardioSessions.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 60.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "🏃", fontSize = 64.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No cardio sessions yet",
                                fontSize = 16.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }
                } else {
                    items(cardioSessions) { session ->
                        CardioCard(session)
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
                    text = "Add Cardio",
                    icon = Icons.AutoMirrored.Filled.DirectionsRun,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    if (showAddDialog) {
        AddCardioDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { type, duration, distance, _, _, _ ->
                viewModel.addCardioSession(type, duration, distance)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun CardioCard(session: CardioSession) {
    GlassCard {
        Column {
            Text(
                text = session.type.uppercase(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text("Duration", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                    Text("${session.duration}min", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Column {
                    Text("Distance", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                    Text("${session.distance}km", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun CardioTypeButton(
    type: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(70.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                Color.White.copy(alpha = 0.15f)
            else
                Color.White.copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = type.replaceFirstChar { it.uppercase() },
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun AddCardioDialog(
    onDismiss: () -> Unit,
    onAdd: (String, Int, Float, Float?, Float?, Int?) -> Unit
) {
    var selectedType by remember { mutableStateOf("bike") }
    var duration by remember { mutableStateOf("") }
    var distance by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = AtlasColors.BackgroundLight,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, AtlasColors.Border)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "ADD CARDIO",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CardioTypeButton("bike", Icons.AutoMirrored.Filled.DirectionsBike, selectedType == "bike", { selectedType = "bike" }, Modifier.weight(1f))
                    CardioTypeButton("run", Icons.AutoMirrored.Filled.DirectionsRun, selectedType == "run", { selectedType = "run" }, Modifier.weight(1f))
                }

                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Duration (min)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(0.3f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White
                    )
                )

                OutlinedTextField(
                    value = distance,
                    onValueChange = { distance = it },
                    label = { Text("Distance (km)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(0.3f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White
                    )
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, Color.White.copy(0.3f))
                    ) {
                        Text("Cancel", color = Color.White)
                    }

                    MinimalButton(
                        onClick = {
                            val dur = duration.toIntOrNull() ?: 0
                            val dist = distance.toFloatOrNull() ?: 0f
                            if (dur > 0 && dist > 0) {
                                onAdd(selectedType, dur, dist, null, null, null)
                            }
                        },
                        text = "Add",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// ============ ACHIEVEMENTS SCREEN ============

@Composable
fun AchievementsScreen(viewModel: AtlasViewModel) {
    val achievements by viewModel.achievements.collectAsState()

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
                    text = "ACHIEVEMENTS",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 2.sp
                )
            }

            items(achievements) { achievement ->
                val isUnlocked = achievement.unlockedAt != null

                GlassCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = achievement.icon,
                            fontSize = 32.sp,
                            modifier = Modifier.alpha(if (isUnlocked) 1f else 0.3f)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = achievement.title.uppercase(),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isUnlocked) Color.White else Color.White.copy(0.5f),
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = achievement.description,
                                fontSize = 12.sp,
                                color = Color.White.copy(0.6f)
                            )

                            if (!isUnlocked) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(0.2f))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(achievement.progress / achievement.target.toFloat())
                                            .fillMaxHeight()
                                            .background(Color.White, CircleShape)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}
