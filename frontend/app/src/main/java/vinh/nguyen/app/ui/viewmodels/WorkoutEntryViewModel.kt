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

    /**
     * Updates the [workoutUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(workoutDetails: WorkoutDetails) {
        workoutUiState =
            WorkoutUiState(workoutDetails = workoutDetails, isEntryValid = validateInput(workoutDetails))
    }

    suspend fun saveWorkout() {
        if (validateInput()) {
            workoutsRepository.insertWorkout(workoutUiState.workoutDetails.toWorkout())
        }
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
