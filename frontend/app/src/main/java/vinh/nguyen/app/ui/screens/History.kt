package vinh.nguyen.app.ui.screens

import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import vinh.nguyen.app.ui.viewmodels.WorkoutViewModel

fun History(
    viewModel: WorkoutViewModel,
    navController: NavController
) {
    viewModel.viewModelScope.launch() {
        val workouts = viewModel.workoutEntryViewModel?.getAllWorkouts()
    }
}