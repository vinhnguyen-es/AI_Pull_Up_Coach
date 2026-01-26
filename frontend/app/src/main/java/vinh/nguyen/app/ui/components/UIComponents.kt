package vinh.nguyen.app.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import vinh.nguyen.app.ui.components.DialogWithImage
import vinh.nguyen.app.ui.viewmodels.WorkoutState
import vinh.nguyen.app.ui.viewmodels.WorkoutViewModel
import vinh.nguyen.app.R
@Composable
fun ConnectionStatusCard(
    modifier: Modifier = Modifier,
    isConnected: Boolean
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            //this is irrelavent for colouring
            containerColor = if (isConnected) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ConnectionStatusIndicator()
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (isConnected) "LIVE" else "OFFLINE",
                //i made a change here should still be white
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ConnectionStatusIndicator() {
    Box(
        modifier = Modifier
            .size(6.dp)
            .background(
                //not touched
                color = MaterialTheme.colorScheme.onSurface,
                shape = RoundedCornerShape(50.dp)
            )
    )
}

@Composable
fun ErrorMessageCard(
    modifier: Modifier = Modifier,
    errorMessage: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = modifier.widthIn(max = 200.dp),
        colors = CardDefaults.cardColors(
            //not really seen by us, colour is fine as is.
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = errorMessage,
                //this one is specifically changed by me
                color = MaterialTheme.colorScheme.onErrorContainer,
                fontSize = 11.sp,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@DrawableRes
fun exerciseIcon(exercise: String): Int {
    return when (exercise) {
        "Pull Ups" -> R.drawable.pull_up_bar_plain
        "Bicep Curls" -> R.drawable.bicep_curl_plain
        "Jumping Jacks" -> R.drawable.jumping_jack_plain
        "Push Ups" -> R.drawable.push_up_plain
        "Sit Ups" -> R.drawable.sit_up_plain
        "Squats" -> R.drawable.squat_plain
        else -> R.drawable.pull_up_bar_plain
    }
}

@Composable
fun ControlPanel(
    modifier: Modifier = Modifier,
    state: WorkoutState,
    onStartReset: () -> Unit,
    onReset: () -> Unit,
    onReconnect: () -> Unit,
    viewModel: WorkoutViewModel,
    navController: NavController
) {
    val colourChoice = when (viewModel.returnExercise()) {
        "Pull Ups"-> 0xFF67EDDE//19CDB9
        "Bicep Curls"-> 0xFFABCEEF//75AEE4
        "Jumping Jacks" -> 0xFFFAD98B//F6BF3B
        "Push Ups" -> 0xFFEfA3A4//E04A4C
        "Sit Ups" -> 0xFFAEA5D7//504294
        "Squats"-> 0xFFF9B0BC//F46C83
        else -> 0xFFFFFFF
    }
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(colourChoice), // dialog background
            contentColor = MaterialTheme.colorScheme.onSurface  // text/icon default
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Title
            PanelTitle(viewModel)

            // Stats Display
            StatsDisplay(state)

            // Control Buttons
            ControlButtons(
                isConnected = state.isConnected,
                isWorkoutActive = state.isWorkoutActive,
                onStartReset = onStartReset,
                onReset = onReset,
                onReconnect = onReconnect,
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}
@Composable
private fun PanelTitle(viewModel: WorkoutViewModel) {
    val exercise = viewModel.returnExercise()
    val iconRes = exerciseIcon(exercise)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 50.dp)
    ) {
        Text(
            text = "$exercise Coach",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Image(
            painter = painterResource(id = iconRes),
            contentDescription = exercise,
            modifier = Modifier.size(100.dp),
            contentScale = ContentScale.Fit
        )
    }
}


//@Composable
//private fun PanelTitle(viewModel: WorkoutViewModel) {
//    Text(
//        text = viewModel.returnExercise() + " Coach",
//        fontSize = 18.sp,
//        fontWeight = FontWeight.Bold,
//        textAlign = TextAlign.Center,
//        color = MaterialTheme.colorScheme.onSurface,
//        modifier = Modifier.padding(top = 50.dp)
//    )
//}

@Composable
private fun StatsDisplay(state: WorkoutState) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Current Reps",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = "${state.repCount}",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Status: ${state.status}",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Text(
            text = "Frames: ${state.framesSent}",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun ControlButtons(
    isConnected: Boolean,
    isWorkoutActive: Boolean,
    onStartReset: () -> Unit,
    onReset: () -> Unit,
    onReconnect: () -> Unit,
    navController: NavController,
    viewModel: WorkoutViewModel
) {
        Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (!isConnected) {
            ReconnectButton(onReconnect)
        } else {
            StartResetButton(isWorkoutActive, onStartReset, viewModel)
        }
        BackButton(
            onClick = {
                navController.navigate("ExercisesDisplay")
                onReset()
            },
            onReset = onReset,
            navController = navController,
            viewModel = viewModel
        )
    }
}


@Composable
private fun ReconnectButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(
            Icons.Default.Refresh,
            contentDescription = null,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "Reconnect",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun StartResetButton(
    isWorkoutActive: Boolean,
    onClick: () -> Unit,
    viewModel: WorkoutViewModel
) {
    val colourChoice = when (viewModel.returnExercise()) {
        "Pull Ups"-> 0xFF19CDB9
        "Bicep Curls"-> 0xFF75AEE4
        "Jumping Jacks" -> 0xFFF6BF3B
        "Push Ups" -> 0xFFE04A4C
        "Sit Ups" -> 0xFF504294
        "Squats"-> 0xFFF46C83
        else -> 0xFFFFFFF
    }
    val secondcolourChoice = when (viewModel.returnExercise()) {
        "Pull Ups"-> 0xFF13988A//19CDB9
        "Bicep Curls"-> 0xFF2573BC//75AEE4
        "Jumping Jacks" -> 0xFFDC9E0A//F6BF3B
        "Push Ups" -> 0xFF9A1A1C//E04A4C
        "Sit Ups" -> 0xFF3A306B//504294
        "Squats"-> 0xFFF03353//F46C83
        else -> 0xFFFFFFF
    }
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isWorkoutActive)
                Color(secondcolourChoice)
            else
                Color(colourChoice)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = if (isWorkoutActive) "RESET" else "START",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun BackButton(
    onClick: () -> Unit,
    onReset: () -> Unit,
    navController: NavController,
    viewModel: WorkoutViewModel
) {
    var showDialog by remember { mutableStateOf(false)}
    val colourChoice = when (viewModel.returnExercise()) {
        "Pull Ups"-> 0xFF19CDB9
        "Bicep Curls"-> 0xFF75AEE4
        "Jumping Jacks" -> 0xFFF6BF3B
        "Push Ups" -> 0xFFE04A4C
        "Sit Ups" -> 0xFF504294
        "Squats"-> 0xFFF46C83
        else -> 0xFFFFFFF
    }
    Button(
        onClick = {showDialog = true
            onReset()
            },
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(colourChoice),
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = "BACK",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
    if (showDialog) {
        DialogWithImage(
            onDismissRequest = { onReset()
                showDialog = false },
            onConfirmation = { onReset()
                showDialog = false},
            drawableRes = when (viewModel.returnExercise()) {
                "Pull Ups"-> R.drawable.pull
                "Bicep Curls"-> R.drawable.bicep_curl
                "Jumping Jacks" -> R.drawable.jumping_jack
                "Push Ups" -> R.drawable.push
                "Sit Ups" -> R.drawable.sit
                "Squats"-> R.drawable.squat
                else -> R.drawable.pull
            },
            imageDescription = viewModel.returnExercise(),
            onReset = onReset,
            navController = navController,
            viewModel = viewModel
        )
    }
}

@Composable
fun DialogWithImage(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    drawableRes: Int,  // Changed from Painter to Int (drawable resource ID)
    imageDescription: String,
    navController: NavController,
    onReset: () -> Unit,
    viewModel: WorkoutViewModel
) {

    Dialog(onDismissRequest = { onDismissRequest() }) {
        // Draw a rectangle shape with rounded corners inside the dialog
        val colourChoice = when (viewModel.returnExercise()) {
            "Pull Ups"-> 0xFF19CDB9
            "Bicep Curls"-> 0xFF75AEE4
            "Jumping Jacks" -> 0xFFF6BF3B
            "Push Ups" -> 0xFFE04A4C
            "Sit Ups" -> 0xFF504294
            "Squats"-> 0xFFF46C83
            else -> 0xFFFFFFF
        }
        val title = viewModel.returnExercise()

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(375.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(colourChoice), // dialog background
                contentColor = MaterialTheme.colorScheme.onSurface  // text/icon default
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row {
                    Image(
                        painter = painterResource(id = drawableRes),  // Changed to painterResource
                        contentDescription = imageDescription,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .height(160.dp)
                    )
                }
                Row{
                    Text(text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center)
                }
                Row {
                    Text(
                        text = buildString {

                            append("Total Reps: ${viewModel.state.value.repCount}\n")
                            append("Total Workout Time: ${viewModel.state.value.completedWorkoutTime ?: "00:00"}\n")

                            val presentText = when (viewModel.state.value.repCount) {
                                0 -> "Ready to start!"
                                in 1..5 -> "Good start!"
                                in 6..15 -> "Great job!"
                                in 21..30 -> "Outstanding!"
                                else -> "You're a champion!"
                            }
                            append("${presentText}")
                        },
                        modifier = Modifier.padding(10.dp),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,

                ) {
                    TextButton(
                        onClick = {
                            onConfirmation()
                            viewModel.clearCompletedTime()
                            navController.navigate("ExercisesDisplay")
                            },

                        modifier = Modifier.padding(8.dp),

                    ) {
                        Text("Confirm",
                                color = MaterialTheme.colorScheme.onSurface//onSurface,

                        )
                    }
                }
            }
        }
    }
}