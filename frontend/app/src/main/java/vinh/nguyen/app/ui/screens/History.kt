package vinh.nguyen.app.ui.screens

import android.graphics.Paint
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import vinh.nguyen.app.ui.components.PanelTitle
import vinh.nguyen.app.ui.viewmodels.WorkoutViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun History(
    viewModel: WorkoutViewModel,
    navController: NavController
) {
    LaunchedEffect(Unit) {
        viewModel.workoutEntryViewModel?.getWorkoutsOnDate()
    }

    if (viewModel.workoutEntryViewModel?.workoutsOnDate!!.size != 0) {
        Row(
            Modifier.fillMaxSize()
        ) {
            Box(
                Modifier.weight(3f)
            ) {
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
            }
            FilterPanel(
                modifier = Modifier
                    .width(180.dp)
                    .fillMaxHeight()
                    .padding(8.dp)
                    .weight(1f),
                viewModel = viewModel,
                navController = navController
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

@Composable
fun FilterPanel(
    modifier: Modifier = Modifier,
    viewModel: WorkoutViewModel,
    navController: NavController
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            // Title
            FiltersTitle(Modifier.align(Alignment.TopCenter))

            // Filter Display
            FiltersDisplay(viewModel, Modifier.align(Alignment.Center))

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
private fun FiltersDisplay(
    viewModel: WorkoutViewModel,
    modifier: Modifier
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        TitleInputPair(
            text = "Start Date",
            viewModel = viewModel,
            type = "startDates"

        )
        TitleInputPair(
            text = "End Date",
            viewModel = viewModel,
            type = "endDates"
        )

        Spacer(modifier = Modifier.height(8.dp))

        TitleInputPair(
            text = "Exercise Type",
            viewModel = viewModel,
            type = "exerciseType"
        )
    }
}

@Composable
private fun TitleInputPair(
    text: String,
    viewModel: WorkoutViewModel,
    type: String
) {
    var expanded by remember { mutableStateOf(false) }
    val workouts: Flow<List<Workout>> = viewModel.workoutEntryViewModel?.getAllWorkouts()!!
    val dates = mutableStateListOf<String>()
    val exercises = mutableStateListOf<String>()

    // TODO: take this logic out of the view
    viewModel.viewModelScope.launch {
        workouts.collect { workouts ->
            dates.clear()
            exercises.clear()
            for (workout in workouts) {
                dates.add(workout.date)
                exercises.add(workout.exercise)
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.secondary.copy()
        )
        OutlinedButton(onClick = { expanded = true }) {
            Text(
                // TODO: Pull from DB the lowest and highest dates and an All Exercises type
                // TODO: Then use the value selected from the dropdown to query the db
                when (type) {
                    "startDates" -> {
                        "Start Date"
                    }
                    "endDates" -> {
                        "End Date"
                    }
                    "exerciseType" -> {
                        "All Exercises"
                    }
                    else -> {
                        "Error constructing dropdown, received dropdown with type: ${type}, expected one of: startDates, endDates, exerciseType"
                    }
                }
            )

        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {expanded = false}
        ) {
            when (type) {
                "startDates", "endDates" -> {
                    for (date in dates) {
                        DropdownMenuItem(
                            text = { Text(date) },
                            onClick = {
                                expanded = false
                            }
                        )
                    }
                }
                "exerciseType" -> {
                    for (exercise in exercises) {
                        DropdownMenuItem(
                            text = { Text(exercise) },
                            onClick = {
                                expanded = false
                            }
                        )
                    }
                }
                else -> {
                    DropdownMenuItem(
                        text = { Text("Error constructing dropdown, received dropdown with type: ${type}, expected one of: startDates, endDates, exerciseType") },
                        onClick = {
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FiltersTitle(modifier: Modifier) {
    Text(
        text = "Filter Workout History",
        fontSize = 18.sp,
        fontWeight = Bold,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.secondary,
        modifier = modifier.padding(top = 50.dp, bottom = 50.dp)
    )
}
