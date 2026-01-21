/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vinh.nguyen.app.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import vinh.nguyen.app.database.Workout
import vinh.nguyen.app.database.WorkoutRepository

/**
 * ViewModel to validate and insert workouts in the Room database.
 */
class WorkoutEntryViewModel(private val workoutsRepository: WorkoutRepository) : ViewModel() {

    /**
     * Holds current workout ui state
     */
    var workoutUiState by mutableStateOf(WorkoutUiState())
        private set

    var totalReps by mutableStateOf(0)
        private set

    var totalWorkouts by mutableStateOf(0)
        private set

    var totalWorkoutTime by mutableStateOf(0)
        private set

    var avgReps by mutableStateOf(0)
        private set


    var totalTimeHHMM by mutableStateOf("00:00")
        private set

    var workoutsOnDate by mutableStateOf(mutableListOf<MutableList<Workout>>())
        private set

    /**
     * Updates the [workoutUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(workoutDetails: WorkoutDetails) {
        workoutUiState =
            WorkoutUiState(workoutDetails = workoutDetails, isEntryValid = validateInput(workoutDetails))
    }

    fun refreshStats() {
        viewModelScope.launch {
            totalReps = workoutsRepository.getTotalReps()

            val lengths = workoutsRepository.getAllLengths()
            totalTimeHHMM = sumLengthsToHHMMSS(lengths)

            totalWorkouts = workoutsRepository.getTotalWorkouts()

            totalWorkoutTime = workoutsRepository.getTotalWorkoutTime()

            avgReps = workoutsRepository.avgRepsPerWorkout()
        }
    }

    private fun sumLengthsToHHMMSS(lengths: List<String>): String {
        var totalSeconds = 0

        for (raw in lengths) {
            val parts = raw.trim().split(":")
            if (parts.size != 2) continue

            val mm = parts[0].toIntOrNull() ?: continue
            val ss = parts[1].toIntOrNull() ?: continue
            if (ss !in 0..59 || mm < 0) continue

            totalSeconds += mm * 60 + ss
        }

        val hh = totalSeconds / 3600
        val mm = (totalSeconds % 3600) / 60
        val ss = totalSeconds % 60

        return String.format("%02d:%02d:%02d", hh, mm, ss)
    }


    fun getWorkoutsOnDate() {
        val workouts: Flow<List<Workout>> = getAllWorkouts()
        viewModelScope.launch {
            workouts.collect {
                    workouts ->
                if (workouts.size != 0) {
                    var date = workouts[0].date

                    var dateIndex = 0
                    for ((i, workout) in workouts.withIndex()) {
                        if (i == 0) {
                            continue
                        }
                        if (workout.date == date) {
                            if (workoutsOnDate.size == 0) {
                                workoutsOnDate.add(mutableListOf())
                            }
                            workoutsOnDate[dateIndex].add(workout)
                        } else {
                            dateIndex++
                            workoutsOnDate[dateIndex] = mutableListOf(workout)
                        }
                    }
                }
            }
        }
    }

    suspend fun saveWorkout() {
        if (validateInput()) {
            workoutsRepository.insertWorkout(workoutUiState.workoutDetails.toWorkout())
        }
    }

    fun getAllWorkouts(): Flow<List<Workout>> {
        return workoutsRepository.getAllWorkoutsStream()
    }

    private fun validateInput(uiState: WorkoutDetails = workoutUiState.workoutDetails): Boolean {
        return with(uiState) {
            exercise.isNotBlank() && reps > 0
        }
    }
}

/**
 * Represents Ui State for an Workout.
 */
data class WorkoutUiState(
    val workoutDetails: WorkoutDetails = WorkoutDetails(),
    val isEntryValid: Boolean = false
)

data class WorkoutDetails(
    val id: Int = 0,
    val exercise: String = "",
    val reps: Int = 0,
    val length: String = "",
    val date: String = ""

)

/**
 * Extension function to convert [WorkoutDetails] to [Workout]. If the value of [WorkoutDetails.price] is
 * not a valid [Double], then the price will be set to 0.0. Similarly if the value of
 * [WorkoutDetails.quantity] is not a valid [Int], then the quantity will be set to 0
 */
fun WorkoutDetails.toWorkout(): Workout = Workout(
    id = id,
    exercise = exercise,
    reps = reps,
    length = length,
    date = date
)

/**
 * Extension function to convert [Workout] to [WorkoutUiState]
 */
fun Workout.toWorkoutUiState(isEntryValid: Boolean = false): WorkoutUiState = WorkoutUiState(
    workoutDetails = this.toWorkoutDetails(),
    isEntryValid = isEntryValid
)

/**
 * Extension function to convert [Workout] to [WorkoutDetails]
 */
fun Workout.toWorkoutDetails(): WorkoutDetails = WorkoutDetails(
    id = id,
    exercise = exercise,
    reps = reps,
    length = length,
    date = date
)
