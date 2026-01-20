package vinh.nguyen.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import vinh.nguyen.app.database.Workout
import vinh.nguyen.app.ui.viewmodels.WorkoutViewModel

@Composable
fun History(
    viewModel: WorkoutViewModel,
    navController: NavController
) {
    viewModel.workoutEntryViewModel?.getWorkoutsOnDate()

    if (viewModel.workoutEntryViewModel?.workoutsOnDate!!.size != 0) {
        for (workouts in viewModel.workoutEntryViewModel?.workoutsOnDate!!) {
            WorkoutDateSection(3, workouts)
        }
    } else {
        Text(
            text = "You are a bozo"
        )
    }
}

@Composable
fun WorkoutDateSection(
    cols: Int = 3,
    workouts: MutableList<Workout>
) {
    // Date (Section Header)
    Text(
        text = workouts[0].date,
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(50f))
            .background(MaterialTheme.colorScheme.onSecondary),
        fontSize = 24.sp,
        fontWeight = Bold,
        textAlign = TextAlign.Center
    )
    for (row in 0 until (workouts.size / cols))
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (col in 0 until cols) {
                WorkoutCard(workouts[row * cols + col], Modifier.weight(1f))
        }
    }
}

@Composable
fun WorkoutCard(
    workout: Workout,
    modifier: Modifier
) {
    Column(
        modifier
            .padding(20.dp)
            .clip(RoundedCornerShape(50f))
            .background(MaterialTheme.colorScheme.onSecondary),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = workout.exercise,
            modifier = Modifier,
            textAlign = TextAlign.Center,
            fontWeight = Bold,
            fontSize = 25.sp,
            color = Color.Black
        )
        Text(
            text = workout.reps.toString(),
            modifier = Modifier,
            textAlign = TextAlign.Center,
            fontWeight = Bold,
            fontSize = 15.sp,
            color = Color.Black
        )
        Text(
            text = workout.length,
            modifier = Modifier,
            textAlign = TextAlign.Center,
            fontWeight = Bold,
            fontSize = 15.sp,
            color = Color.Black
        )
    }
}
