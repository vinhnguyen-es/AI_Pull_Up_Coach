package vinh.nguyen.app.database
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Database class with a singleton Instance object.
 */
//CHANGE TO VERSION 2
@Database(entities = [Workout::class], version = 2, exportSchema = false)
abstract class WorkoutDatabase : RoomDatabase() {

    abstract fun workoutEntryDao(): WorkoutEntryDao

    companion object {
        @Volatile
        private var Instance: WorkoutDatabase? = null

        fun getDatabase(context: Context): WorkoutDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, WorkoutDatabase::class.java, "workout_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}