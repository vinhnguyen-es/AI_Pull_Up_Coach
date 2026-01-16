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

    @Query("SELECT * from workouts ORDER BY exercise ASC")
    fun getAllWorkoutsStream(): Flow<List<Workout>>
}