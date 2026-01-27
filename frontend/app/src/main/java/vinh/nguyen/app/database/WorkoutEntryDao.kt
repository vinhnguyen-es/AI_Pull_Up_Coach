package vinh.nguyen.app.database
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutEntryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWorkout(workout: Workout)

    @Update
    suspend fun updateWorkout(workout: Workout)

    @Delete
    suspend fun deleteWorkout(workout: Workout)

    @Query("SELECT * from workouts WHERE id = :id")
    fun getWorkoutStream(id: Int): Flow<Workout?>

    @Query("SELECT * from workouts ORDER BY date DESC")
    fun getAllWorkoutsStream(): Flow<List<Workout>>


    @Query("SELECT length FROM workouts")
    suspend fun getAllLengths(): List<String>



    @Query("SELECT SUM(reps) from workouts")
    suspend fun getTotalReps(): Int

    @Query("SELECT COUNT(reps) from workouts")
    suspend fun getTotalWorkouts(): Int

    @Query("SELECT SUM(length) from workouts")
    suspend fun getTotalWorkoutTime(): Int
    //pull into a list, then pulls the numbers out of the strings
    // he already did it for stats...

    @Query("SELECT AVG(reps) FROM workouts ")
    suspend fun avgRepsPerWorkout(): Int


    /**
     * cumulative reps ✅
     * total workout number ✅
     * total workout time ✅
     * average resps per workout
     */

}