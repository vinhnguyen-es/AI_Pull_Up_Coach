package vinh.nguyen.app.ui.screens

import android.graphics.Paint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import vinh.nguyen.app.database.Workout
import vinh.nguyen.app.ui.viewmodels.WorkoutViewModel

@Composable
fun History(
    viewModel: WorkoutViewModel,
    navController: NavController
) {
    viewModel.workoutEntryViewModel?.getWorkoutsOnDate()

    val TAG = "WorkoutViewModel"
    Log.w(TAG, "Size: ${viewModel.workoutEntryViewModel?.workoutsOnDate!!.size}")

    if (viewModel.workoutEntryViewModel?.workoutsOnDate!!.size != 0) {
        Log.w(TAG, "HERE 1")
        Log.w(TAG, viewModel.workoutEntryViewModel?.workoutsOnDate.toString())
        for (workouts in viewModel.workoutEntryViewModel?.workoutsOnDate!!) {
            Log.w(TAG, workouts.toString())
            WorkoutDateSection(3, workouts, Modifier.fillMaxSize().padding(top = 10.dp))
        }
    } else {
        Log.w(TAG, "HERE 2")
        Box (
            Modifier.fillMaxSize(),
        ) {
            Text(
                text = "No Workouts Logged",
                fontSize = 50.sp,
                modifier = Modifier.align(Alignment.Center)
            )
            BackButton(
                modifier = Modifier.align(Alignment.BottomCenter),
                onClick = {
                    navController.popBackStack()
                },
            )
        }
    }
}

@Composable
private fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Bottom
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "BACK",
                fontWeight = Bold,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
    }
    }
}

@Composable
fun WorkoutDateSection(
    cols: Int = 3,
    workouts: MutableList<Workout>,
    modifier: Modifier = Modifier
) {
    // Date (Section Header)
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
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
