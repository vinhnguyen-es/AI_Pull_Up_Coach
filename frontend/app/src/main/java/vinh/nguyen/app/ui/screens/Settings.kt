package vinh.nguyen.app.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import vinh.nguyen.app.R
import vinh.nguyen.app.SettingsCog
import vinh.nguyen.app.ui.viewmodels.WorkoutViewModel

@Composable
fun Settings(
    viewModel: ViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
    onToggleTheme: () -> Unit,
    darkTheme: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.onTertiary)
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Settings",
                textAlign = TextAlign.Center,
                fontWeight = Bold,
                fontSize = 30.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Settings content area
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(20.dp)
        ) {
            // Theme switch on top left
            ThemeSwitch(
                darkTheme = darkTheme,
                onToggle = onToggleTheme
            )

            // Add more settings here if needed
            Spacer(modifier = Modifier.weight(1f))
        }

        // Back button on bottom right
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.End
        ) {
            BackButton(
                onClick = {
                    navController.navigate("ExercisesDisplay")
                }
            )
        }
    }
}

@Composable
fun ThemeSwitch(
    darkTheme: Boolean,
    onToggle: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(16.dp)
    ) {
        if (darkTheme){
            Text("Dark Theme",
                color = MaterialTheme.colorScheme.onBackground)
        } else {
            Text("Light Theme",
                color = MaterialTheme.colorScheme.onBackground)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = darkTheme,
            onCheckedChange = { onToggle() },
            thumbContent = {
                Icon(
                    imageVector = if (darkTheme) Icons.Filled.DarkMode else Icons.Filled.WbSunny,
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize),
                    tint = if (darkTheme) Color.Blue else Color.Yellow
                )
            }
        )
    }
}

@Composable
private fun BackButton(
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(120.dp)
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
//
//@Composable
//fun Settings(
//    viewModel: ViewModel,
//    navController: NavController,
//    modifier: Modifier = Modifier,
//    onToggleTheme: () -> Unit,
//    darkTheme: Boolean
//) {
//    Column(
//        //this is the banner colour
//        modifier = Modifier.background(MaterialTheme.colorScheme.onPrimary)
//    ) {
//        Row(
//            modifier = modifier
//                .padding(20.dp)
//                .fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(
//                text = "Settings",
//                textAlign = TextAlign.Center,
//                fontWeight = Bold,
//                fontSize = 30.sp
//            )
//        }
//
//        Row(){
//            ThemeSwitch(darkTheme = darkTheme,
//                onToggle = onToggleTheme)
//        }
//        Row(){
//            BackButton(
//                onClick = {
//                    navController.navigate("ExercisesDisplay")
//                }
//            )
//        }
//    }
//
//}
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
//    }
//
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
//            .fillMaxWidth()
//            .height(60.dp),
//        colors = ButtonDefaults.buttonColors(
//            containerColor = MaterialTheme.colorScheme.primary
//        ),
//        shape = RoundedCornerShape(12.dp)
//    ) {
//        Text(
//            text = "BACK",
//            fontWeight = FontWeight.Bold,
//            fontSize = 16.sp
//        )
//    }
//}
//
