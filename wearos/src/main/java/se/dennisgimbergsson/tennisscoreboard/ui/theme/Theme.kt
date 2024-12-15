package se.dennisgimbergsson.tennisscoreboard.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColorScheme(
    primary = Purple200,
    //primaryVariant = Purple700,
    secondary = Teal200,
    surfaceTint = Color.White,
    background = Color.Black,
)

private val LightColorPalette = lightColorScheme(
    primary = Purple500,
    //primaryVariant = Purple700,
    secondary = Teal200,
    surfaceTint = Color.Blue,
    background = Color.Black,

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun TennisScoreboardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) = MaterialTheme(
    colorScheme = when {
        darkTheme -> DarkColorPalette
        else -> LightColorPalette
    },
    typography = Typography,
    shapes = Shapes,
    content = content
)