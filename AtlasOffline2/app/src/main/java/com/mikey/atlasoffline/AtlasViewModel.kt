package com.mikey.atlasoffline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.delay


class AtlasViewModelFactory(private val dao: WorkoutDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AtlasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AtlasViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

data class WorkoutStats(
    val totalWorkouts: Int = 0,
    val totalTime: Long = 0,
    val totalExercises: Int = 0,
    val currentStreak: Int = 0,
    val personalRecords: Int = 0,
    val totalVolume: Float = 0f,
    val level: Int = 1,
    val currentXP: Int = 0,
    val nextLevelXP: Int = 500,
    val rankTitle: String = "RECRUIT",
    val rankSubtitle: String = "Aspiring Warrior"
)

data class ChartData(
    val weeklyWorkouts: Map<String, Int> = emptyMap(),
    val volumeProgress: List<Pair<String, Float>> = emptyList(),
    val muscleGroups: Map<String, Int> = emptyMap()
)

data class ActiveExercise(
    val name: String,
    val sets: MutableList<SetData> = mutableListOf(),
    val restTime: Int = 90
)

data class SetData(
    val reps: Int,
    val weight: Float
)

object GreekRanks {
    data class Rank(
        val level: Int,
        val title: String,
        val subtitle: String,
        val xpRequired: Int
    )

    val ranks = listOf(
        Rank(1, "RECRUIT", "Aspiring Warrior", 0),
        Rank(5, "HOPLITE", "Foot Soldier", 2000),
        Rank(10, "ENOMOTARCH", "Squad Leader", 5000),
        Rank(15, "LOCHAGOS", "Captain", 10000),
        Rank(20, "PENTEKONTER", "Commander of Fifty", 17000),
        Rank(25, "TAXIARCH", "Regiment Commander", 26000),
        Rank(30, "STRATEGOS", "General", 37000),
        Rank(35, "POLEMARCH", "War Leader", 50000),
        Rank(40, "SPARTAN", "Elite Warrior", 65000),
        Rank(50, "HERO", "Legendary Fighter", 85000),
        Rank(60, "DEMIGOD", "Half-Divine", 110000),
        Rank(75, "TITAN", "Primordial Force", 145000),
        Rank(100, "OLYMPIAN", "Divine Champion", 200000)
    )

    fun getRankForLevel(level: Int): Rank {
        return ranks.lastOrNull { it.level <= level } ?: ranks.first()
    }

    fun getNextRank(level: Int): Rank? {
        return ranks.firstOrNull { it.level > level }
    }
}

class AtlasViewModel(private val dao: WorkoutDao) : ViewModel() {

    private val _stats = MutableStateFlow(WorkoutStats())
    val stats: StateFlow<WorkoutStats> = _stats.asStateFlow()

    private val _chartData = MutableStateFlow(ChartData())
    val chartData: StateFlow<ChartData> = _chartData.asStateFlow()

    private val _recentExercises = MutableStateFlow<List<ExerciseEntry>>(emptyList())
    val recentExercises: StateFlow<List<ExerciseEntry>> = _recentExercises.asStateFlow()

    private val _currentSessionId = MutableStateFlow<Int?>(null)
    val currentSessionId: StateFlow<Int?> = _currentSessionId.asStateFlow()

    private val _workoutStartTime = MutableStateFlow<LocalDateTime?>(null)
    val workoutStartTime: StateFlow<LocalDateTime?> = _workoutStartTime.asStateFlow()

    private val _activeExercises = MutableStateFlow<List<ActiveExercise>>(emptyList())
    val activeExercises: StateFlow<List<ActiveExercise>> = _activeExercises.asStateFlow()

    private val _restTimerActive = MutableStateFlow(false)
    val restTimerActive: StateFlow<Boolean> = _restTimerActive.asStateFlow()

    private val _restTimeRemaining = MutableStateFlow(0)
    val restTimeRemaining: StateFlow<Int> = _restTimeRemaining.asStateFlow()

    private val _currentExerciseForTimer = MutableStateFlow<String?>(null)
    val currentExerciseForTimer: StateFlow<String?> = _currentExerciseForTimer.asStateFlow()

    private val _cardioSessions = MutableStateFlow<List<CardioSession>>(emptyList())
    val cardioSessions: StateFlow<List<CardioSession>> = _cardioSessions.asStateFlow()

    private val _bodyMeasurements = MutableStateFlow<List<BodyMeasurement>>(emptyList())
    val bodyMeasurements: StateFlow<List<BodyMeasurement>> = _bodyMeasurements.asStateFlow()

    private val _achievements = MutableStateFlow<List<Achievement>>(emptyList())
    val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()

    private val _personalRecords = MutableStateFlow<List<PersonalRecord>>(emptyList())
    val personalRecords: StateFlow<List<PersonalRecord>> = _personalRecords.asStateFlow()

    private val _templates = MutableStateFlow<List<WorkoutTemplate>>(emptyList())
    val templates: StateFlow<List<WorkoutTemplate>> = _templates.asStateFlow()

    private val _allSessions = MutableStateFlow<List<WorkoutSession>>(emptyList())
    val allSessions: StateFlow<List<WorkoutSession>> = _allSessions.asStateFlow()

    private val _newPRDetected = MutableStateFlow<Pair<String, Float>?>(null)
    val newPRDetected: StateFlow<Pair<String, Float>?> = _newPRDetected.asStateFlow()

    init {
        loadStats()
        loadRecentData()
        loadPRs()
        loadChartData()
        loadAchievements()
        loadMeasurements()
        loadTemplates()
        loadAllSessions()
    }

    private fun loadStats() {
        viewModelScope.launch {
            try {
                val totalWorkouts = dao.getTotalWorkoutCount()
                val totalTime = dao.getTotalWorkoutTime() ?: 0L
                val totalVolume = dao.getTotalVolume() ?: 0f
                val totalPRs = dao.getTotalPRCount()

                // Get ALL completed sessions for XP calculation
                val allSessions = dao.getAllSessions().first()

                // Calculate total XP from completed workouts
                var totalXP = 0
                allSessions.forEach { session ->
                    if (session.endTime?.isNotEmpty() == true && session.totalExercises > 0) {
                        // Get exercises for this session to count sets
                        val xp = calculateWorkoutXP(
                            exercises = session.totalExercises,
                            sets = session.totalExercises * 3, // Estimate 3 sets per exercise
                            volume = session.totalVolume,
                            duration = session.totalDuration
                        )
                        totalXP += xp
                    }
                }

                val level = calculateLevelFromXP(totalXP)
                val currentRank = GreekRanks.getRankForLevel(level)
                val nextRank = GreekRanks.getNextRank(level)

                _stats.value = WorkoutStats(
                    totalWorkouts = totalWorkouts,
                    totalTime = totalTime,
                    currentStreak = 0,
                    personalRecords = totalPRs,
                    totalVolume = totalVolume,
                    level = level,
                    currentXP = totalXP,
                    nextLevelXP = nextRank?.xpRequired ?: (totalXP + 1000),
                    rankTitle = currentRank.title,
                    rankSubtitle = currentRank.subtitle
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun calculateLevelFromXP(xp: Int): Int {
        return when {
            xp < 500 -> 1
            xp < 1000 -> 2
            xp < 2000 -> 3 + ((xp - 1000) / 500)
            xp < 5000 -> 5 + ((xp - 2000) / 600)
            xp < 10000 -> 10 + ((xp - 5000) / 800)
            xp < 20000 -> 15 + ((xp - 10000) / 1000)
            xp < 40000 -> 20 + ((xp - 20000) / 1500)
            xp < 70000 -> 30 + ((xp - 40000) / 2000)
            xp < 110000 -> 40 + ((xp - 70000) / 2500)
            else -> 50 + ((xp - 110000) / 3000)
        }.coerceAtMost(100)
    }

    private fun loadRecentData() {
        viewModelScope.launch {
            try {
                dao.getRecentExercises(10).collect { exercises ->
                    _recentExercises.value = exercises
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadPRs() {
        viewModelScope.launch {
            try {
                dao.getAllPRs().collect { prs ->
                    _personalRecords.value = prs
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadChartData() {
        viewModelScope.launch {
            try {
                // Get last 7 days workout data
                val sessions = dao.getAllSessions().first()
                val today = LocalDateTime.now()
                val weekDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

                val weeklyWorkouts = weekDays.associateWith { 0 }.toMutableMap()

                sessions.filter { it.endTime?.isNotEmpty() == true }.forEach { session ->
                    try {
                        val startTime = session.startTime ?: return@forEach
                        val sessionDate = LocalDateTime.parse(startTime)
                        val dayOfWeek = sessionDate.dayOfWeek.name.take(3).lowercase()
                            .replaceFirstChar { it.uppercase() }

                        if (dayOfWeek in weekDays) {
                            weeklyWorkouts[dayOfWeek] = (weeklyWorkouts[dayOfWeek] ?: 0) + 1
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }


                // Volume progress (last 10 workouts)
                val volumeData = sessions
                    .filter { (it.endTime?.isNotEmpty() == true) && it.totalVolume > 0 }
                    .takeLast(10)
                    .mapIndexed { index, session ->
                        "W${index + 1}" to session.totalVolume
                    }


                _chartData.value = ChartData(
                    weeklyWorkouts = weeklyWorkouts,
                    volumeProgress = volumeData,
                    muscleGroups = emptyMap()
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun loadAchievements() {
        viewModelScope.launch {
            try {
                dao.getAllAchievements().collect { achievements ->
                    if (achievements.isEmpty()) {
                        AchievementsData.defaultAchievements.forEach { achievement ->
                            dao.insertAchievement(achievement)
                        }
                    }
                    _achievements.value = achievements.ifEmpty { AchievementsData.defaultAchievements }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadMeasurements() {
        viewModelScope.launch {
            try {
                dao.getAllMeasurements().collect { measurements ->
                    _bodyMeasurements.value = measurements
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadTemplates() {
        viewModelScope.launch {
            try {
                dao.getAllTemplates().collect { templates ->
                    _templates.value = templates
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadAllSessions() {
        viewModelScope.launch {
            try {
                dao.getAllSessions().collect { sessions ->
                    _allSessions.value = sessions
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun startWorkout() {
        viewModelScope.launch {
            try {
                val session = WorkoutSession(
                    startTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                    endTime = "",
                    totalDuration = 0,
                    totalExercises = 0,
                    totalVolume = 0f
                )
                val sessionId = dao.insertSession(session).toInt()
                _currentSessionId.value = sessionId
                _workoutStartTime.value = LocalDateTime.now()
                _activeExercises.value = emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun finishWorkout() {
        viewModelScope.launch {
            try {
                val sessionId = _currentSessionId.value ?: return@launch
                val startTime = _workoutStartTime.value ?: return@launch
                val endTime = LocalDateTime.now()
                val duration = ChronoUnit.MINUTES.between(startTime, endTime)

                var totalVolume = 0f
                var totalSets = 0
                var exerciseCount = 0

                _activeExercises.value.forEach { exercise ->
                    if (exercise.sets.isNotEmpty()) {
                        exerciseCount++
                        val reps = exercise.sets.joinToString(",") { it.reps.toString() }
                        val weights = exercise.sets.joinToString(",") { it.weight.toString() }

                        exercise.sets.forEach { set ->
                            totalVolume += set.reps * set.weight
                            totalSets++
                        }

                        dao.insertExercise(
                            ExerciseEntry(
                                sessionId = sessionId,
                                exerciseName = exercise.name,
                                sets = exercise.sets.size,
                                reps = reps,
                                weights = weights,
                                restTime = exercise.restTime
                            )
                        )
                    }
                }

                val xpGained = calculateWorkoutXP(
                    exercises = exerciseCount,
                    sets = totalSets,
                    volume = totalVolume,
                    duration = duration
                )

                val session = WorkoutSession(
                    id = sessionId,
                    startTime = startTime.format(DateTimeFormatter.ISO_DATE_TIME),
                    endTime = endTime.format(DateTimeFormatter.ISO_DATE_TIME),
                    totalDuration = duration,
                    totalExercises = exerciseCount,
                    totalVolume = totalVolume
                )
                dao.updateSession(session)

                _currentSessionId.value = null
                _workoutStartTime.value = null
                _activeExercises.value = emptyList()

                checkAchievements()
                loadStats()
                loadChartData()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            // Force reload with delay
            delay(500)
            loadStats()
            loadChartData()
            loadRecentData()

        }
    }

    private fun calculateWorkoutXP(
        exercises: Int,
        sets: Int,
        volume: Float,
        duration: Long
    ): Int {
        if (exercises == 0 || sets == 0) return 0

        var xp = 0
        xp += exercises * 20
        xp += sets * 5
        xp += (volume / 10).toInt()
        xp += (duration.coerceAtMost(60) * 2).toInt()

        if (duration >= 20 && exercises >= 3) {
            xp += 50
        }

        return xp
    }

    fun addExercise(exerciseName: String) {
        val current = _activeExercises.value.toMutableList()
        current.add(ActiveExercise(name = exerciseName))
        _activeExercises.value = current
    }

    fun removeExercise(index: Int) {
        val current = _activeExercises.value.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _activeExercises.value = current
        }
    }

    fun addSet(exerciseIndex: Int, reps: Int, weight: Float) {
        val current = _activeExercises.value.toMutableList()
        if (exerciseIndex in current.indices) {
            current[exerciseIndex].sets.add(SetData(reps, weight))
            _activeExercises.value = current

            val exerciseName = current[exerciseIndex].name
            checkWeightRecommendation(exerciseName, reps, weight)
            checkForPR(exerciseName, reps, weight)

            val smartRestTime = calculateSmartRestTime(exerciseName, reps, weight)
            startRestTimer(smartRestTime, exerciseName)
        }
    }

    fun removeSet(exerciseIndex: Int, setIndex: Int) {
        val current = _activeExercises.value.toMutableList()
        if (exerciseIndex in current.indices && setIndex in current[exerciseIndex].sets.indices) {
            current[exerciseIndex].sets.removeAt(setIndex)
            _activeExercises.value = current
        }
    }

    fun calculateSmartRestTime(exerciseName: String, reps: Int, weight: Float): Int {
        val isCompound = listOf(
            "Squat", "Deadlift", "Bench Press", "Row", "Press",
            "Pull-up", "Chin-up", "Dip"
        ).any { exerciseName.contains(it, ignoreCase = true) }

        val isHeavy = reps <= 5
        val isMedium = reps in 6..10

        return when {
            isCompound && isHeavy -> 180
            isCompound && isMedium -> 150
            isCompound -> 120
            isHeavy -> 120
            isMedium -> 90
            else -> 60
        }
    }

    fun startRestTimer(seconds: Int, exerciseName: String) {
        viewModelScope.launch {
            _restTimerActive.value = true
            _currentExerciseForTimer.value = exerciseName
            _restTimeRemaining.value = seconds

            repeat(seconds) {
                kotlinx.coroutines.delay(1000)
                _restTimeRemaining.value = _restTimeRemaining.value - 1
            }

            _restTimerActive.value = false
            _currentExerciseForTimer.value = null
        }
    }

    fun stopRestTimer() {
        _restTimerActive.value = false
        _currentExerciseForTimer.value = null
        _restTimeRemaining.value = 0
    }

    private fun checkWeightRecommendation(exerciseName: String, reps: Int, weight: Float) {
        // Placeholder
    }

    private fun checkForPR(exerciseName: String, reps: Int, weight: Float) {
        viewModelScope.launch {
            try {
                val existingPR = dao.getPRForExercise(exerciseName)
                val oneRepMax = calculateOneRepMax(weight, reps)

                if (existingPR == null || oneRepMax > existingPR.oneRepMax) {
                    dao.insertPR(
                        PersonalRecord(
                            exerciseName = exerciseName,
                            weight = weight,
                            reps = reps,
                            oneRepMax = oneRepMax
                        )
                    )

                    _newPRDetected.value = exerciseName to oneRepMax
                    unlockAchievement("Golden Fleece")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun clearPRNotification() {
        _newPRDetected.value = null
    }

    private fun calculateOneRepMax(weight: Float, reps: Int): Float {
        return if (reps == 1) weight else weight * (1 + reps / 30f)
    }

    private fun checkAchievements() {
        viewModelScope.launch {
            try {
                val totalWorkouts = dao.getTotalWorkoutCount()

                when (totalWorkouts) {
                    1 -> unlockAchievement("First Steps")
                    10 -> unlockAchievement("Spartan Discipline")
                    50 -> unlockAchievement("Marathon Runner")
                    100 -> unlockAchievement("Herculean Effort")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun unlockAchievement(title: String) {
        try {
            val achievement = dao.getAchievementByTitle(title)
            if (achievement != null && achievement.unlockedAt == null) {
                val updated = achievement.copy(
                    unlockedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
                )
                dao.updateAchievement(updated)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addCardioSession(type: String, duration: Int, distance: Float) {
        viewModelScope.launch {
            try {
                dao.insertCardio(
                    CardioSession(
                        type = type,
                        duration = duration,
                        distance = distance
                    )
                )
                dao.getRecentCardio(20).collect { sessions ->
                    _cardioSessions.value = sessions
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addBodyMeasurement(
        weight: Float?,
        bodyFat: Float?,
        chest: Float?,
        waist: Float?,
        hips: Float?,
        biceps: Float?,
        thighs: Float?,
        calves: Float?
    ) {
        viewModelScope.launch {
            try {
                dao.insertMeasurement(
                    BodyMeasurement(
                        weight = weight,
                        bodyFat = bodyFat,
                        chest = chest,
                        waist = waist,
                        hips = hips,
                        biceps = biceps,
                        thighs = thighs,
                        calves = calves
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveCurrentAsTemplate(name: String, description: String) {
        viewModelScope.launch {
            try {
                val exercises = _activeExercises.value
                if (exercises.isEmpty()) return@launch

                val exercisesJson = exercises.joinToString(",") { exercise ->
                    """{"name":"${exercise.name}","sets":${exercise.sets.size},"rest":${exercise.restTime}}"""
                }

                val template = WorkoutTemplate(
                    name = name,
                    description = description,
                    exercises = "[$exercisesJson]",
                    category = "Custom"
                )

                dao.insertTemplate(template)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadTemplate(template: WorkoutTemplate) {
        viewModelScope.launch {
            try {
                startWorkout()
                val exerciseNames = extractExerciseNames(template.exercises)
                exerciseNames.forEach { name ->
                    addExercise(name)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadPrebuiltTemplate(templateName: String) {
        viewModelScope.launch {
            try {
                startWorkout()

                val exercises = when (templateName) {
                    "Push Day" -> listOf(
                        "Barbell Bench Press",
                        "Incline Dumbbell Press",
                        "Dumbbell Shoulder Press",
                        "Lateral Raise",
                        "Tricep Dips",
                        "Cable Pushdown"
                    )
                    "Pull Day" -> listOf(
                        "Deadlift",
                        "Barbell Row",
                        "Pull-Ups",
                        "Lat Pulldown",
                        "Barbell Curl",
                        "Hammer Curl"
                    )
                    "Leg Day" -> listOf(
                        "Barbell Squat",
                        "Romanian Deadlift",
                        "Leg Press",
                        "Leg Curl",
                        "Leg Extension",
                        "Calf Raise"
                    )
                    else -> emptyList()
                }

                exercises.forEach { name ->
                    addExercise(name)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteTemplate(template: WorkoutTemplate) {
        viewModelScope.launch {
            try {
                dao.deleteTemplate(template)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun extractExerciseNames(json: String): List<String> {
        val regex = """"name":"([^"]+)"""".toRegex()
        return regex.findAll(json).map { it.groupValues[1] }.toList()
    }

    fun resetAllData() {
        viewModelScope.launch {
            try {
                dao.deleteAllSessions()
                dao.deleteAllExercises()
                dao.deleteAllCardio()
                dao.deleteAllMeasurements()
                dao.deleteAllPRs()
                dao.deleteAllAchievements()

                AchievementsData.defaultAchievements.forEach { achievement ->
                    dao.insertAchievement(achievement)
                }

                _currentSessionId.value = null
                _workoutStartTime.value = null
                _activeExercises.value = emptyList()

                loadStats()
                loadRecentData()
                loadPRs()
                loadChartData()
                loadAchievements()
                loadMeasurements()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

