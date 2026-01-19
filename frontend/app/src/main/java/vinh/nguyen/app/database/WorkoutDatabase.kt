package vinh.nguyen.app.database
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Database class with a singleton Instance object.
 */
@Database(entities = [Workout::class], version = 2, exportSchema = false)
abstract class WorkoutDatabase : RoomDatabase() {

    abstract fun workoutEntryDao(): WorkoutEntryDao

    companion object {
        @Volatile
        private var Instance: WorkoutDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Example: Add a new integer column 'age' with a default value of 0
                database.execSQL("ALTER TABLE workouts ADD COLUMN date CHAR255 NOT NULL DEFAULT ''")
            }
        }


        fun getDatabase(context: Context): WorkoutDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, WorkoutDatabase::class.java, "workout_database")
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}