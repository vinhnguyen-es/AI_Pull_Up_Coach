package vinh.nguyen.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import vinh.nguyen.app.R
import vinh.nguyen.app.database.Workout
import vinh.nguyen.app.ui.viewmodels.WorkoutViewModel

@Composable
fun History(
    viewModel: WorkoutViewModel,
    navController: NavController
) {
    LaunchedEffect(Unit) {
        viewModel.workoutEntryViewModel?.getWorkoutsOnDate()
    }

    if (viewModel.workoutEntryViewModel?.workoutsOnDate!!.size != 0) {
        Box() {
            LazyColumn(
                Modifier.background(MaterialTheme.colorScheme.background)
            ) {
                for (workouts in viewModel.workoutEntryViewModel?.workoutsOnDate!!) {
                    item {
                        WorkoutDateSection(3, workouts, Modifier.padding(top = 10.dp))
                    }
                }
                item {
                    Spacer(
                        Modifier
                            .height(60.dp),
                    )
                }
            }

            BackButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                onClick = {
                    navController.popBackStack()
                },
            )
        }
    } else {
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
    Button(
        onClick = onClick,
        modifier = modifier
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

@Composable
fun WorkoutDateSection(
    cols: Int = 3,
    workouts: MutableList<Workout>,
    modifier: Modifier = Modifier
) {
    // Date (Section Header)
    Column(
        Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .clip(RoundedCornerShape(50f))
                .background(Color(0xFF5AA846))
                .fillMaxWidth()
                .height(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = workouts[0].date,
                fontSize = 24.sp,
                fontWeight = Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        val rowCount = (workouts.size + cols - 1) / cols
        for (row in 0 until rowCount) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (col in 0 until cols) {
                    if (row * cols + col < workouts.size) {
                        var buttonColor = when (workouts[row * cols + col].exercise) {
                            "Pull Ups" -> Color(0xFF19CDB9)  // Purple for Pull Ups
                            "Bicep Curls" -> Color(0xFF75AEE4)  // Teal for Bicep Curls
                            "Jumping Jacks" -> Color(0xFFF6BF3B)  // Red for Jumping Jacks
                            "Push Ups" -> Color(0xFFE04A4C)  // Cyan for Push Ups
                            "Sit Ups" -> Color(0xFF504294)  // Yellow for Sit Ups
                            "Squats" -> Color(0xFFF46C83)  // Mint for Squats
                            else -> Color.Gray
                        }
                        var resource = when (workouts[row * cols + col].exercise) {
                            "Pull Ups" -> R.drawable.pull
                            "Bicep Curls" -> R.drawable.bicep_curl
                            "Jumping Jacks" -> R.drawable.jumping_jack
                            "Push Ups" -> R.drawable.push
                            "Sit Ups" -> R.drawable.sit
                            "Squats" -> R.drawable.squat
                            else -> R.drawable.pull
                        }

                        WorkoutCard(workouts[row * cols + col],
                            Modifier.weight(1f),
                            buttonColor,
                            resource)
                    }
                }
            }
        }
    }
}

@Composable
fun WorkoutCard(
    workout: Workout,
    modifier: Modifier,
    colour: Color,
    resource: Int
) {
    Column(
        modifier
            .padding(20.dp)
            .clip(RoundedCornerShape(50f))
            .background(colour),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        Image(
            painter = painterResource(resource),
            contentDescription = null,
            modifier = Modifier
        )
        Text(
            text = workout.exercise,
            modifier = Modifier,
            textAlign = TextAlign.Center,
            fontWeight = Bold,
            fontSize = 25.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            text = workout.reps.toString(),
            modifier = Modifier,
            textAlign = TextAlign.Center,
            fontWeight = Bold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            text = workout.length,
            modifier = Modifier,
            textAlign = TextAlign.Center,
            fontWeight = Bold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}
