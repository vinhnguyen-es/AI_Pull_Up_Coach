package vinh.nguyen.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext


private val LightColorScheme = lightColorScheme(
    primary = primaryBlueLight,
    secondary = primaryBlueDark,
    tertiary = primaryGreenLight,
    onPrimary = white,
    onSecondary = primaryDark,
    onTertiary = Purple80,
    background = PurpleGrey80, //THIS IS JUST A TEST
    onErrorContainer = Color(0xFF93000A),
    onSurface = Color(0xFF1A1B21)
    //^^ is like a black colurs
    //val onSurfaceLight = Color(0xFF1A1B21)
    //WAIT MAYBE DO USE THE FILE TO OVERWRITE ALL POSSIBLE COLOURS IDK...
    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)
private val DarkColorScheme = darkColorScheme(
    /*primary = primaryBlueDark,
    secondary = primaryDark,
    tertiary = primaryGreenDark
    //tehse r all the same right now just for testing....
     */
    primary = primaryBlueDark,//primaryBlueLight,
    secondary = primaryDark,
    tertiary = primaryGreenLight,
    onPrimary = white,
    onSecondary = PurpleGrey80,
    onTertiary = Purple80,
    background = primaryBlueDark, //THIS IS JUST A TEST
    onErrorContainer = Color(0xFF93000A),
    onSurface = Color(0xFF1A1B21),

)


@Composable
fun AppTheme(
    //darkTheme: Boolean = isSystemInDarkTheme(),
    darkTheme: Boolean = false,
    // Dynamic color is available on Android 12+
    //dynamicColor: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
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