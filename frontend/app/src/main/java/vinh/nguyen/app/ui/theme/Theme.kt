package vinh.nguyen.app.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = BluePrimaryLight,
    secondary = BluePrimaryDark,
    tertiary = GreenPrimaryLight,

    onPrimary = White,
    onSecondary = BlueAccentLight,
    onTertiary = Purple80,

    background = PurpleGrey80,
    onSurface = Color(0xFF1A1B21),

    onErrorContainer = ErrorDark,

//    onBackground = pullUps,
//    surface = bicepCurls,
//    surfaceVariant = sitUps,
//    inversePrimary = pushUps,
//    inverseSurface = jumpingJacks,
//    error = squats,
//    onError = stats,
//    outline = history





)

private val DarkColorScheme = darkColorScheme(
    primary = BluePrimaryDark,
    secondary = BlueAccentLight,
    tertiary = GreenPrimaryLight,

    onPrimary = White,
    onSecondary = BlueAccentDark,
    onTertiary = Purple80,

    background = BluePrimaryDark,
    onSurface = Color(0xFF1A1B21),

    onErrorContainer = ErrorDark,
)

@Composable
fun AppTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}



//package vinh.nguyen.app.ui.theme
//
//import android.app.Activity
//import android.os.Build
//import androidx.compose.foundation.isSystemInDarkTheme
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.darkColorScheme
//import androidx.compose.material3.dynamicDarkColorScheme
//import androidx.compose.material3.dynamicLightColorScheme
//import androidx.compose.material3.lightColorScheme
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//
//
//private val LightColorScheme = lightColorScheme(
//    primary = primaryBlueLight,
//    secondary = primaryBlueDark,
//    tertiary = primaryGreenLight,
//    onPrimary = white,
//    onSecondary = primaryDark,
//    onTertiary = Purple80,
//    background = PurpleGrey80, //THIS IS JUST A TEST
//    onErrorContainer = Color(0xFF93000A),
//    onSurface = Color(0xFF1A1B21)
//)
//private val DarkColorScheme = darkColorScheme(
//    primary = primaryBlueDark,//primaryBlueLight,
//    secondary = primaryDark,
//    tertiary = primaryGreenLight,
//    onPrimary = white,
//    onSecondary = whatevs,//PurpleGrey80,
//    onTertiary = Purple80,
//    background = primaryBlueDark, //THIS IS JUST A TEST
//    onErrorContainer = Color(0xFF93000A),
//    onSurface = Color(0xFF1A1B21),
//
//)
//
//
//@Composable
//fun AppTheme(
//    //darkTheme: Boolean = isSystemInDarkTheme(),
//    darkTheme: Boolean = false,
//    // Dynamic color is available on Android 12+
//    //dynamicColor: Boolean = true,
//    dynamicColor: Boolean = false,
//    content: @Composable () -> Unit
//) {
//
//    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }
//
//        darkTheme -> DarkColorScheme
//        else -> LightColorScheme
//    }
//
//    MaterialTheme(
//        colorScheme = colorScheme,
//        typography = Typography,
//        content = content
//    )
//}