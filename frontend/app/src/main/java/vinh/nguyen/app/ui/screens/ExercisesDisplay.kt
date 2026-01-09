package vinh.nguyen.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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

@Composable
fun ExercisesDisplay(navController: NavController, modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier.background(Color.White)
    ) {
        Text(
            text = stringResource(R.string.title),
            modifier = modifier.padding(20.dp).fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontWeight = Bold,
            fontSize = 30.sp
        )
        Column(
            modifier.background(Color.Black),
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
                            0 -> R.drawable.pull_ups
                            1 -> R.drawable.bicep_curls
                            2 -> R.drawable.jumping_jacks
                            3 -> R.drawable.push_ups
                            4 -> R.drawable.sit_ups
                            5 -> R.drawable.squats
                            else -> R.drawable.pull_ups
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
                        val resourceDescription = text + " Image"

                        ExerciseCard(
                            navController,
                            text = text,
                            resource = resource,
                            resourceDescription = resourceDescription,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseCard(navController: NavController, text: String, resource: Int, resourceDescription: String, modifier: Modifier = Modifier) {
    Button(
        onClick = { navController.navigate("PullUps") },
        modifier
            .padding(20.dp)
            .clip(RoundedCornerShape(50f))
            .background(Color.White),
        colors = ButtonDefaults.buttonColors(Color.White),
        shape = RectangleShape
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
                color = Color.Black
            )
        }
    }
}

