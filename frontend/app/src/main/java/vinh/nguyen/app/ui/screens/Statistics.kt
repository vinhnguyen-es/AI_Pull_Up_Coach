//package vinh.nguyen.app.ui.screens
//
//import androidx.compose.animation.core.Spring
//import androidx.compose.animation.core.animateDpAsState
//import androidx.compose.animation.core.animateFloatAsState
//import androidx.compose.animation.core.spring
//import androidx.compose.animation.core.tween
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.interaction.MutableInteractionSource
//import androidx.compose.foundation.interaction.collectIsPressedAsState
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.DarkMode
//import androidx.compose.material.icons.filled.Settings
//import androidx.compose.material.icons.filled.WbSunny
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Switch
//import androidx.compose.material3.SwitchDefaults
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.draw.shadow
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.graphicsLayer
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.font.FontWeight.Companion.Bold
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.ViewModel
//import androidx.navigation.NavController
//import vinh.nguyen.app.R
//import vinh.nguyen.app.SettingsCog
//import vinh.nguyen.app.TextCard
//import vinh.nguyen.app.ui.viewmodels.WorkoutViewModel
//@Composable
//fun Statistics(
//    viewModel: WorkoutViewModel,
//    navController: NavController,
//    modifier: Modifier = Modifier
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(MaterialTheme.colorScheme.background)
//    ) {
//        //This is the banner//
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .background(MaterialTheme.colorScheme.onTertiary)
//                .padding(20.dp),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(
//                text = "Most recent workout statistics",
//                textAlign = TextAlign.Center,
//                fontWeight = Bold,
//                fontSize = 30.sp,
//                color = MaterialTheme.colorScheme.onBackground
//            )
//        }
//
//        // Settings content area
//        //was a column...
//        Row(
//            modifier = Modifier
//                .weight(1f)
//                .padding(20.dp)
//        ) {
//            viewModel.workoutEntryViewModel?.refreshStats()
//            Text(
//                text = "Total Reps : ${viewModel.workoutEntryViewModel?.totalReps}\n" + //${viewModel.workoutEntryViewModel?.totalReps}
//                        "Total number of workouts: ${viewModel.workoutEntryViewModel?.totalWorkouts}\n" +
//                        "Total Cumulative Workout Time: ${viewModel.workoutEntryViewModel?.totalWorkoutTime}\n" + //${viewModel.workoutEntryViewModel?.totalTimeHHMM?: "00:00"}
//                        "Average reps per workout: ${viewModel.workoutEntryViewModel?.avgReps}\n"
//                ,
//                modifier = modifier.weight(1f)
//            )
//
//            // Add more settings here if needed
//            Spacer(modifier = Modifier.weight(1f))
//        }
//
//        // Back button on bottom right
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(20.dp),
//            horizontalArrangement = Arrangement.Center
//        ) {
//            Button(
//                onClick = { navController.navigate("ExercisesDisplay") },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(50.dp),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = MaterialTheme.colorScheme.primary
//                ),
//                shape = RoundedCornerShape(12.dp),
//                elevation = ButtonDefaults.buttonElevation(
//                    defaultElevation = 6.dp,
//                    pressedElevation = 2.dp
//                )
//            ) {
//                Text(
//                    text = "BACK",
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 16.sp
//                )
//            }
//        }
//    }
//
//}
//
//@Composable
//private fun BackButton(
//    onClick: () -> Unit
//) {
//    Button(
//        onClick = onClick,
//        modifier = Modifier
//            .width(120.dp)
//            .height(50.dp),
//        colors = ButtonDefaults.buttonColors(
//            containerColor = MaterialTheme.colorScheme.primary
//        ),
//        shape = RoundedCornerShape(12.dp),
//        elevation = ButtonDefaults.buttonElevation(
//            defaultElevation = 6.dp,
//            pressedElevation = 2.dp
//        )
//    ) {
//        Text(
//            text = "BACK",
//            fontWeight = FontWeight.Bold,
//            fontSize = 16.sp
//        )
//    }
//}
//
///**
// * title = "Stats",
// *                     content = listOf("Total Reps: ${viewModel.workoutEntryViewModel?.totalReps}",
// *                         "Total Workout Time: ${viewModel.workoutEntryViewModel?.totalTimeHHMM}"),
// *                     modifier = modifier.weight(1f),
// */
package vinh.nguyen.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import vinh.nguyen.app.ui.viewmodels.WorkoutViewModel

@Composable
fun Statistics(
    viewModel: WorkoutViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Banner at the top
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.onTertiary)
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Statistics",
                textAlign = TextAlign.Center,
                fontWeight = Bold,
                fontSize = 30.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Statistics content area with cards
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            viewModel.workoutEntryViewModel?.refreshStats()

            // Total Reps Card
            StatCard(
                title = "Total Reps",
                value = "${viewModel.workoutEntryViewModel?.totalReps ?: "You got this! Do some reps!"}",//0
                backgroundColor = Color(0xFF19CDB9),
                modifier = Modifier.fillMaxWidth()
            )

            // Total Workouts Card
            StatCard(
                title = "Total Workouts",
                value = "${viewModel.workoutEntryViewModel?.totalWorkouts ?: "Lets do some work outs"}",//0
                backgroundColor = Color(0xFF75AEE4),
                modifier = Modifier.fillMaxWidth()
            )

            // Total Workout Time Card
            StatCard(
                title = "Total Workout Time",
                value = "${viewModel.workoutEntryViewModel?.totalTimeHHMM ?: "Start now!"}",
                backgroundColor = Color(0xFFF6BF3B),
                modifier = Modifier.fillMaxWidth()
            )

            // Average Reps Card
            StatCard(
                title = "Average Reps per Workout",
                value = "${viewModel.workoutEntryViewModel?.avgReps ?: ""}",
                backgroundColor = Color(0xFFE04A4C),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
        }

        // Back button at the bottom
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { navController.navigate("ExercisesDisplay") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 2.dp
                )
            ) {
                Text(
                    text = "BACK",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            textAlign = TextAlign.Center,
            fontWeight = Bold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(1.dp))
        Text(
            text = value,
            textAlign = TextAlign.Center,
            fontWeight = Bold,
            fontSize = 28.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}