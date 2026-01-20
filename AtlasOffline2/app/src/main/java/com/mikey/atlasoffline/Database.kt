package com.mikey.atlasoffline

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// ============ ENTITIES ============

@Entity(tableName = "workout_sessions")
data class WorkoutSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startTime: String,
    val endTime: String?,
    val totalDuration: Long = 0,
    val totalExercises: Int = 0,
    val totalVolume: Float = 0f, // Total kg lifted
    val notes: String = ""
)

@Entity(tableName = "exercises")
data class ExerciseEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sessionId: Int,
    val exerciseName: String,
    val sets: Int,
    val reps: String,
    val weights: String,
    val timestamp: String = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
    val restTime: Int = 0, // seconds
    val notes: String = ""
)

@Entity(tableName = "cardio_sessions")
data class CardioSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,
    val duration: Int,
    val distance: Float,
    val speed: Float? = null,
    val incline: Float? = null,
    val toughness: Int? = null,
    val timestamp: String = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
    val calories: Int? = null
)

@Entity(tableName = "body_measurements")
data class BodyMeasurement(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: String = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
    val weight: Float? = null,
    val bodyFat: Float? = null,
    val chest: Float? = null,
    val waist: Float? = null,
    val hips: Float? = null,
    val biceps: Float? = null,
    val thighs: Float? = null,
    val calves: Float? = null,
    val neck: Float? = null,
    val shoulders: Float? = null
)

@Entity(tableName = "personal_records")
data class PersonalRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val exerciseName: String,
    val weight: Float,
    val reps: Int,
    val oneRepMax: Float, // Calculated 1RM
    val timestamp: String = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
)

@Entity(tableName = "workout_templates")
data class WorkoutTemplate(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val exercises: String, // JSON: [{"name":"Squat","sets":4,"reps":"8-10","weight":0}]
    val category: String = "Custom", // Push, Pull, Legs, Upper, Lower, Full Body
    val createdAt: String = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
)


@Entity(tableName = "achievements")
data class Achievement(  // Make sure it says "data class"
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val icon: String,
    val target: Int,
    val progress: Int = 0,
    val unlockedAt: String? = null
)


@Entity(tableName = "rest_timers")
data class RestTimer(
    @PrimaryKey val exerciseName: String,
    val defaultRestSeconds: Int = 90
)

// ============ DAO ============

@Dao
interface WorkoutDao {
    // Sessions
    @Insert
    suspend fun insertSession(session: WorkoutSession): Long

    @Update
    suspend fun updateSession(session: WorkoutSession)

    @Query("SELECT * FROM workout_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<WorkoutSession>>

    @Query("SELECT COUNT(*) FROM workout_sessions WHERE endTime != ''")
    suspend fun getTotalWorkoutCount(): Int

    @Query("SELECT SUM(totalDuration) FROM workout_sessions WHERE endTime != ''")
    suspend fun getTotalWorkoutTime(): Long?

    @Query("SELECT SUM(totalVolume) FROM workout_sessions WHERE endTime != '' AND id IN (SELECT MAX(id) FROM workout_sessions GROUP BY date(startTime))")
    suspend fun getTotalVolume(): Float?


    // Exercises
    @Insert
    suspend fun insertExercise(exercise: ExerciseEntry)

    @Query("SELECT * FROM exercises ORDER BY id DESC LIMIT :limit")
    fun getRecentExercises(limit: Int): Flow<List<ExerciseEntry>>

    @Query("SELECT * FROM exercises WHERE exerciseName = :exerciseName ORDER BY id DESC LIMIT 1")
    suspend fun getLastExercise(exerciseName: String): ExerciseEntry?

    // Cardio
    @Insert
    suspend fun insertCardio(cardio: CardioSession)

    @Query("SELECT * FROM cardio_sessions ORDER BY id DESC LIMIT :limit")
    fun getRecentCardio(limit: Int): Flow<List<CardioSession>>

    // Measurements
    @Insert
    suspend fun insertMeasurement(measurement: BodyMeasurement)

    @Query("SELECT * FROM body_measurements ORDER BY timestamp DESC")
    fun getAllMeasurements(): Flow<List<BodyMeasurement>>

    // Personal Records
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPR(pr: PersonalRecord)

    @Query("SELECT * FROM personal_records ORDER BY oneRepMax DESC")
    fun getAllPRs(): Flow<List<PersonalRecord>>

    @Query("SELECT * FROM personal_records WHERE exerciseName = :exerciseName LIMIT 1")
    suspend fun getPRForExercise(exerciseName: String): PersonalRecord?

    @Query("SELECT COUNT(*) FROM personal_records")
    suspend fun getTotalPRCount(): Int

    // Templates
    @Insert
    suspend fun insertTemplate(template: WorkoutTemplate): Long

    @Update
    suspend fun updateTemplate(template: WorkoutTemplate)

    @Delete
    suspend fun deleteTemplate(template: WorkoutTemplate)

    @Query("SELECT * FROM workout_templates ORDER BY createdAt DESC")
    fun getAllTemplates(): Flow<List<WorkoutTemplate>>

    @Query("SELECT * FROM workout_templates WHERE category = :category")
    fun getTemplatesByCategory(category: String): Flow<List<WorkoutTemplate>>

    // Achievements
    @Insert
    suspend fun insertAchievement(achievement: Achievement)

    @Update
    suspend fun updateAchievement(achievement: Achievement)

    @Query("SELECT * FROM achievements ORDER BY id ASC")
    fun getAllAchievements(): Flow<List<Achievement>>

    @Query("SELECT * FROM achievements WHERE title = :title LIMIT 1")
    suspend fun getAchievementByTitle(title: String): Achievement?

    // Reset All Data
    @Query("DELETE FROM workout_sessions")
    suspend fun deleteAllSessions()

    @Query("DELETE FROM exercises")
    suspend fun deleteAllExercises()

    @Query("DELETE FROM cardio_sessions")
    suspend fun deleteAllCardio()

    @Query("DELETE FROM body_measurements")
    suspend fun deleteAllMeasurements()

    @Query("DELETE FROM personal_records")
    suspend fun deleteAllPRs()

    @Query("DELETE FROM achievements")
    suspend fun deleteAllAchievements()
}


// ============ DATABASE ============

@Database(
    entities = [
        WorkoutSession::class,
        ExerciseEntry::class,
        CardioSession::class,
        BodyMeasurement::class,
        PersonalRecord::class,
        WorkoutTemplate::class,
        Achievement::class,
        RestTimer::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AtlasDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao

    companion object {
        @Volatile
        private var INSTANCE: AtlasDatabase? = null

        fun getDatabase(context: android.content.Context): AtlasDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AtlasDatabase::class.java,
                    "atlas_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// ============ EXPANDED EXERCISE DATABASE ============

object ExerciseDatabase {
    val exercises = listOf(
        // CHEST
        "Barbell Bench Press", "Incline Bench Press", "Decline Bench Press",
        "Dumbbell Bench Press", "Incline Dumbbell Press", "Decline Dumbbell Press",
        "Chest Fly", "Incline Fly", "Cable Crossover", "Pec Deck",
        "Push-Ups", "Diamond Push-Ups", "Wide Push-Ups",

        // BACK
        "Deadlift", "Romanian Deadlift", "Sumo Deadlift",
        "Barbell Row", "Bent-Over Row", "T-Bar Row", "Pendlay Row",
        "Seated Cable Row", "One-Arm Dumbbell Row", "Chest Supported Row",
        "Lat Pulldown", "Wide Grip Pulldown", "Close Grip Pulldown",
        "Pull-Ups", "Chin-Ups", "Neutral Grip Pull-Ups",
        "Face Pulls", "Hyperextensions", "Back Extensions",

        // SHOULDERS
        "Overhead Press", "Military Press", "Push Press",
        "Dumbbell Shoulder Press", "Arnold Press", "Seated Press",
        "Lateral Raise", "Front Raise", "Rear Delt Fly",
        "Cable Lateral Raise", "Upright Row", "Shrugs",
        "Dumbbell Shrugs", "Barbell Shrugs",

        // LEGS
        "Barbell Squat", "Front Squat", "Goblet Squat", "Box Squat",
        "Leg Press", "Hack Squat", "Bulgarian Split Squat",
        "Walking Lunges", "Reverse Lunges", "Stationary Lunges",
        "Leg Curl", "Seated Leg Curl", "Lying Leg Curl",
        "Leg Extension", "Romanian Deadlift", "Stiff Leg Deadlift",
        "Hip Thrust", "Glute Bridge", "Single Leg Hip Thrust",
        "Calf Raise", "Seated Calf Raise", "Donkey Calf Raise",

        // ARMS - BICEPS
        "Barbell Curl", "EZ Bar Curl", "Dumbbell Curl",
        "Hammer Curl", "Preacher Curl", "Concentration Curl",
        "Cable Curl", "Incline Dumbbell Curl", "Spider Curl",

        // ARMS - TRICEPS
        "Close-Grip Bench Press", "Tricep Extension", "Overhead Tricep Extension",
        "Skull Crushers", "Tricep Dips", "Bench Dips",
        "Cable Pushdown", "Rope Pushdown", "Diamond Push-Ups",

        // CORE
        "Plank", "Side Plank", "Plank with Reach",
        "Crunches", "Bicycle Crunches", "Reverse Crunches",
        "Leg Raises", "Hanging Leg Raises", "Knee Raises",
        "Russian Twists", "Ab Wheel Rollout", "Cable Crunches",
        "Mountain Climbers", "Dead Bug", "Bird Dog"
    ).sorted()

    val muscleGroups = mapOf(
        "Chest" to listOf("Bench Press", "Fly", "Push-Up", "Pec Deck"),
        "Back" to listOf("Row", "Pull", "Deadlift", "Lat"),
        "Shoulders" to listOf("Press", "Raise", "Shrug"),
        "Legs" to listOf("Squat", "Lunge", "Leg", "Calf", "Hip", "Glute"),
        "Biceps" to listOf("Curl"),
        "Triceps" to listOf("Tricep", "Dip", "Extension", "Skull", "Pushdown"),
        "Core" to listOf("Plank", "Crunch", "Ab", "Core", "Twist", "Leg Raise")
    )

    fun getExercisesByMuscleGroup(group: String): List<String> {
        val keywords = muscleGroups[group] ?: return emptyList()
        return exercises.filter { exercise ->
            keywords.any { keyword -> exercise.contains(keyword, ignoreCase = true) }
        }
    }
}

// ============ ACHIEVEMENTS DATA ============

object AchievementsData {
    val defaultAchievements = listOf(
        Achievement(
            title = "Hercules' First Labor",
            description = "Complete your first workout",
            icon = "💪",
            target = 1
        ),
        Achievement(
            title = "Atlas Rising",
            description = "Complete 10 workouts",
            icon = "🏛️",
            target = 10
        ),
        Achievement(
            title = "Spartan Warrior",
            description = "Complete 50 workouts",
            icon = "⚔️",
            target = 50
        ),
        Achievement(
            title = "Olympian",
            description = "Complete 100 workouts",
            icon = "👑",
            target = 100
        ),
        Achievement(
            title = "Zeus' Strength",
            description = "Lift 10,000 kg total volume",
            icon = "⚡",
            target = 10000
        ),
        Achievement(
            title = "Marathon Runner",
            description = "Run 42 km total",
            icon = "🏃",
            target = 42
        ),
        Achievement(
            title = "Achilles' Speed",
            description = "Complete 7 workouts in 7 days",
            icon = "🔥",
            target = 7
        ),
        Achievement(
            title = "Titan's Endurance",
            description = "Work out for 100 hours total",
            icon = "⏱️",
            target = 100
        ),
        Achievement(
            title = "Golden Fleece",
            description = "Beat 10 personal records",
            icon = "🥇",
            target = 10
        ),
        Achievement(
            title = "Pantheon Member",
            description = "Reach Level 20",
            icon = "🏺",
            target = 20
        )
    )
}
