package vinh.nguyen.app

import android.R.attr.scaleX
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewModelScope
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import vinh.nguyen.app.ui.viewmodels.WorkoutViewModel

@Composable
fun ExercisesDisplay(navController: NavController,
                     darkTheme: Boolean,
                     onToggleTheme: () -> Unit,
                     modifier: Modifier = Modifier, viewModel: WorkoutViewModel) {
    Column(
        //this is the banner colour
        modifier = Modifier.background(MaterialTheme.colorScheme.onTertiary)
    ) {
        Row(
            modifier = modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.title),
                textAlign = TextAlign.Center,
                fontWeight = Bold,
                fontSize = 30.sp,
                color = MaterialTheme.colorScheme.onBackground

            )
            SettingsCog(onClick = { navController.navigate("Settings") })
                //{navController.navigate("Settings")}

//            ThemeSwitch(
//                darkTheme = darkTheme,
//                onToggle = onToggleTheme
//            )
        }

        // The row below the top banner
        Row() {
            Column(
                //the background
                Modifier.background(MaterialTheme.colorScheme.background)
                    .weight(4f),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (row in 0..2) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = modifier.weight(1f)
                    ) {
                        for (column in 0..1) {
                            var resource = when (2 * row + column) {
                                0 -> R.drawable.pull
                                1 -> R.drawable.bicep_curl
                                2 -> R.drawable.jumping_jack
                                3 -> R.drawable.push
                                4 -> R.drawable.sit
                                5 -> R.drawable.squat
                                else -> R.drawable.pull
                            }
                            var text = when (2 * row + column) {
                                0 -> "Pull Ups"
                                1 -> "Bicep Curls"
                                2 -> "Jumping Jacks"
                                3 -> "Push Ups"
                                4 -> "Sit Ups"
                                5 -> "Squats"
                                else -> "Pull Up"
                            }

                            var buttonColor = when (2 * row + column) {
                                0 -> Color(0xFF19CDB9)  // Purple for Pull Ups
                                1 -> Color(0xFF75AEE4)  // Teal for Bicep Curls
                                2 -> Color(0xFFF6BF3B)  // Red for Jumping Jacks
                                3 -> Color(0xFFE04A4C)  // Cyan for Push Ups
                                4 -> Color(0xFF504294)  // Yellow for Sit Ups
                                5 -> Color(0xFFF46C83)  // Mint for Squats
                                else -> Color.Gray
                            }
                            val resourceDescription = text + " Image"

                            IconCard(
                                navController,
                                text = text,
                                resource = resource,
                                resourceDescription = resourceDescription,
                                modifier = Modifier.weight(1f),
                                viewModel = viewModel,
                                buttonColor = buttonColor
                            )
                        }
                    }
                }
            }

            // The column the with the stats, summary, history and settings buttons
            Column(
                modifier = Modifier.fillMaxWidth(0.3f)
                    .weight(1f).background(MaterialTheme.colorScheme.background),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Stats
                viewModel.workoutEntryViewModel?.refreshStats()
                TextCard(
                    title = "Stats",
                    content = listOf("Total Reps: ${viewModel.workoutEntryViewModel?.totalReps}", "Total Workout Time: ${viewModel.workoutEntryViewModel?.totalTimeHHMM}"),
                    modifier = modifier.weight(1f),
                )
                IconCard(
                    navController = navController,
                    text = "Statistics",
                    resource = R.drawable.summary,
                    resourceDescription = "Last Workout Statistics",
                    modifier = modifier.weight(1f),
                    viewModel = viewModel,
                    buttonColor = Color(0xFFEDAF6D),
                    onClick =  {
                        navController.navigate("Statistics")
                    }
                )
                IconCard(
                    navController = navController,
                    text = "History",
                    resource = R.drawable.history,
                    resourceDescription = "Previous Workout History",
                    modifier = modifier.weight(1f),
                    viewModel = viewModel,
                    buttonColor = Color(0xFFF46E01),
                    onClick =  {
                        navController.navigate("History")
                    }
                )
            }
        }
    }
}


@Composable
fun IconCard(
    navController: NavController,
    text: String,
    resource: Int,
    resourceDescription: String,
    modifier: Modifier = Modifier,
    viewModel: WorkoutViewModel,
    buttonColor: Color = MaterialTheme.colorScheme.onSecondary,
    onClick: () -> Unit = {
        navController.navigate("WorkoutScreen")
        viewModel.changeScreen(text)
    }
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )

    val elevation by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 8.dp,
        animationSpec = tween(100)
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .padding(20.dp)
            .graphicsLayer {
                scaleX = scale
                this.scaleY = scale
            },
        colors = ButtonDefaults.buttonColors(buttonColor),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = elevation,
            pressedElevation = 2.dp
        ),
        interactionSource = interactionSource
    ) {
        Column() {
            Image(
                painter = painterResource(resource),
                contentDescription = resourceDescription,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(4f)
            )
            Text(
                text = text,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                textAlign = TextAlign.Center,
                fontWeight = Bold,
                fontSize = 25.sp,
                //color = Color.Black
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun TextCard(
    title: String,
    content: List<String>,
    modifier: Modifier
) {
    Column(
        modifier
            .padding(20.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(50f)
            )
            .clip(RoundedCornerShape(50f))
            .background(Color(0xFF5AA846)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = title,
            modifier = Modifier,
            textAlign = TextAlign.Center,
            fontWeight = Bold,
            fontSize = 25.sp,
            //color = Color.Black
            color = MaterialTheme.colorScheme.onBackground
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (text in content) {
                Text(
                    text = text,
                    modifier = Modifier,
                    textAlign = TextAlign.Center,
                    fontWeight = Bold,
                    fontSize = 15.sp,
                    //color = Color.Black
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

class Toggle(){
    var isDark: Boolean = true

    fun whichToggle(): Boolean{
        return isDark
    }
    fun switchToggle(){
        isDark = !isDark
    }
}

@Composable
fun SettingsCog(onClick: () -> Unit, modifier: Modifier = Modifier){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(16.dp)
    ){
        IconButton(
            onClick = onClick,
            modifier = modifier
        ){
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = "Settings",
                modifier = Modifier.size(48.dp),
                tint = Color.Black  // or MaterialTheme.colorScheme.onBackground
            )
        }

    }
}


//
//@Composable
//fun ThemeSwitch(
//    darkTheme: Boolean,
//    onToggle: () -> Unit
//) {
//    Row(
//        verticalAlignment = Alignment.CenterVertically,
//        modifier = Modifier.padding(16.dp)
//    ) {
//
//        if (darkTheme){
//            Text("Dark Theme")
//        } else {
//            Text("Light Theme")
//        }
//        Spacer(modifier = Modifier.width(2.dp))
//        Switch(
//            checked = darkTheme,
//            onCheckedChange = { onToggle() },
//            thumbContent = {
//                Icon(
//                    imageVector = if (darkTheme) Icons.Filled.DarkMode else Icons.Filled.WbSunny,
//                    contentDescription = null,
//                    modifier = Modifier.size(SwitchDefaults.IconSize),
//                    tint = if (darkTheme) Color.Blue else Color.Yellow  // Custom colors!
//                )
//            }
//        )
//            }
//
//    }
