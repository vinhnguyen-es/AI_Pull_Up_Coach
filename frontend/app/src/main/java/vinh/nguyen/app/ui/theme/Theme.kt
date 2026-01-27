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
    dynamicColor: Boolean = true, // Change colours based on wallpaper
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