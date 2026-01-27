@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package vinh.nguyen.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import kotlinx.coroutines.flow.Flow
import vinh.nguyen.app.R
import vinh.nguyen.app.database.Workout
import vinh.nguyen.app.ui.viewmodels.WorkoutViewModel

/**
 * - No collecting Flows inside DropdownMenu or viewModelScope.launch in Composables.
 * - One source of truth for filters: startDate/endDate/exercise in History().
 * - Dropdown buttons show selected value immediately (no submit button required).
 * - Filtering is derived state -> recomposition is reliable.
 * - Date range uses lexicographic comparison (works for yyyy-MM-dd).
 */
@Composable
fun History(
    viewModel: WorkoutViewModel,
    navController: NavController
) {
    var startDate by rememberSaveable { mutableStateOf<String?>(null) }
    var endDate by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedExercise by rememberSaveable { mutableStateOf<String?>(null) } // null = all

    val workoutsFlow: Flow<List<Workout>> =
        viewModel.workoutEntryViewModel?.getAllWorkouts()
            ?: return

    val allWorkouts by workoutsFlow.collectAsState(initial = emptyList())

    val allDates = remember(allWorkouts) {
        allWorkouts.map { it.date }.distinct().sorted()
    }
    val allExercises = remember(allWorkouts) {
        allWorkouts.map { it.exercise }.distinct().sorted()
    }

    val filteredWorkouts by remember(allWorkouts, startDate, endDate, selectedExercise) {
        derivedStateOf {
            allWorkouts.filter { w ->
                val exerciseOk = selectedExercise.isNullOrBlank() || w.exercise == selectedExercise
                val dateOk = withinSelectedDates(w.date, startDate, endDate)
                exerciseOk && dateOk
            }
        }
    }

    val groupedByDate: List<MutableList<Workout>> by remember(filteredWorkouts) {
        derivedStateOf {
            filteredWorkouts
                .groupBy { it.date }
                .toSortedMap()
                .values
                .map { it.toMutableList() }
        }
    }

    Row(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Box(Modifier.weight(3f)) {
            if (groupedByDate.isNotEmpty()) {
                LazyColumn(Modifier.background(MaterialTheme.colorScheme.background)) {
                    groupedByDate.forEach { dayWorkouts ->
                        item {
                            WorkoutDateSection(
                                cols = 3,
                                workouts = dayWorkouts,
                                modifier = Modifier.padding(top = 10.dp)
                            )
                        }
                    }
                    item { Spacer(Modifier.height(60.dp)) }
                }
            } else {
                Box(Modifier.fillMaxSize()) {
                    Text(
                        text = "No Workouts Logged",
                        fontSize = 50.sp,
                        modifier = Modifier.align(Alignment.Center)
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
            navController = navController,
            allDates = allDates,
            allExercises = allExercises,
            startDate = startDate,
            endDate = endDate,
            selectedExercise = selectedExercise,
            onStartDate = { startDate = it },
            onEndDate = { endDate = it },
            onExercise = { selectedExercise = it }
        )
    }
}

@Composable
private fun FilterPanel(
    modifier: Modifier = Modifier,
    navController: NavController,
    allDates: List<String>,
    allExercises: List<String>,
    startDate: String?,
    endDate: String?,
    selectedExercise: String?,
    onStartDate: (String?) -> Unit,
    onEndDate: (String?) -> Unit,
    onExercise: (String?) -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            FiltersTitle(Modifier.align(Alignment.TopCenter))

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Start Date
                TitleInputPair(
                    label = "Start Date",
                    placeholder = "Start Date",
                    selected = startDate,
                    options = allDates,
                    onSelect = { onStartDate(it) }
                )

                // End Date
                TitleInputPair(
                    label = "End Date",
                    placeholder = "End Date",
                    selected = endDate,
                    options = allDates,
                    onSelect = { onEndDate(it) }
                )

                // Exercise
                TitleInputPair(
                    label = "Exercise Type",
                    placeholder = "All Exercises",
                    selected = selectedExercise,
                    options = allExercises,
                    // allow clearing selection by choosing "All Exercises" if you want:
                    onSelect = { onExercise(it) },
                    allowClear = true
                )
            }

            BackButton(
                modifier = Modifier.align(Alignment.BottomCenter),
                onClick = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun TitleInputPair(
    label: String,
    placeholder: String,
    selected: String?,
    options: List<String>,
    onSelect: (String?) -> Unit,
    allowClear: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.secondary
        )

        OutlinedButton(onClick = { expanded = true }) {
            Text(selected ?: placeholder)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (allowClear) {
                val isSelected = selected.isNullOrBlank()
                DropdownMenuItem(
                    text = {
                        Text(
                            placeholder,
                            color = if (isSelected) MaterialTheme.colorScheme.primary
                            else LocalContentColor.current
                        )
                    },
                    onClick = {
                        onSelect(null)
                        expanded = false
                    }
                )
            }

            options.forEach { option ->
                val isSelected = option == selected
                DropdownMenuItem(
                    text = {
                        Text(
                            option,
                            color = if (isSelected) MaterialTheme.colorScheme.primary
                            else LocalContentColor.current
                        )
                    },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
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
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
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
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Date header
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
                text = workouts.firstOrNull()?.date ?: "",
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
                    val idx = row * cols + col
                    if (idx < workouts.size) {
                        val w = workouts[idx]
                        val (buttonColor, resource) = workoutStyle(w.exercise)
                        WorkoutCard(
                            workout = w,
                            modifier = Modifier.weight(1f),
                            colour = buttonColor,
                            resource = resource
                        )
                    }
                }
            }
        }
    }
}

private fun workoutStyle(exercise: String): Pair<Color, Int> {
    val color = when (exercise) {
        "Pull Ups" -> Color(0xFF19CDB9)
        "Bicep Curls" -> Color(0xFF75AEE4)
        "Jumping Jacks" -> Color(0xFFF6BF3B)
        "Push Ups" -> Color(0xFFE04A4C)
        "Sit Ups" -> Color(0xFF504294)
        "Squats" -> Color(0xFFF46C83)
        else -> Color.Gray
    }
    val res = when (exercise) {
        "Pull Ups" -> R.drawable.pull
        "Bicep Curls" -> R.drawable.bicep_curl
        "Jumping Jacks" -> R.drawable.jumping_jack
        "Push Ups" -> R.drawable.push
        "Sit Ups" -> R.drawable.sit
        "Squats" -> R.drawable.squat
        else -> R.drawable.pull
    }
    return color to res
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
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Image(
            painter = painterResource(resource),
            contentDescription = null,
            Modifier.size(72.dp)
        )
        Text(
            text = workout.exercise,
            textAlign = TextAlign.Center,
            fontWeight = Bold,
            fontSize = 25.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            text = workout.reps.toString(),
            textAlign = TextAlign.Center,
            fontWeight = Bold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            text = workout.length,
            textAlign = TextAlign.Center,
            fontWeight = Bold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )
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
fun withinSelectedDates(date: String, start: String?, end: String?): Boolean {
    if (start.isNullOrBlank() || end.isNullOrBlank()) return true
    // yyyy-mm-dd is sorted lexicographically
    return date >= start && date <= end
}