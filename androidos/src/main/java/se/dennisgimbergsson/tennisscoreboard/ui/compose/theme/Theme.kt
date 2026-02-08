package se.dennisgimbergsson.tennisscoreboard.ui.compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import se.dennisgimbergsson.tennisscoreboard.R

data class Colors(
    val text: Color = Color.Unspecified,
    val invertedText: Color = Color.Unspecified,
    val background: Color = Color.Unspecified,
    val containerColor: Color = Color.Unspecified,
)

@Composable
fun darkColors() = Colors(
    text = colorResource(android.R.color.white),
    invertedText = colorResource(android.R.color.white),
    background = colorResource(R.color.black_94),
    containerColor = colorResource(R.color.black_75),
)

@Composable
fun lightColors() = Colors(
    text = colorResource(android.R.color.black),
    invertedText = colorResource(android.R.color.black),
    background = colorResource(R.color.white_13),
    containerColor = colorResource(R.color.white_25),
)

private val LocalColors = staticCompositionLocalOf {
    Colors()
}

@Composable
private fun darkColorScheme() = MaterialTheme.colorScheme.copy(
    primary = Color(0xFFBB86FC),
    primaryContainer = Color(0xFF3700B3),
    secondary = Color(0xFF03DAC5),
    background = Color.Black,
    surface = colorResource(R.color.darkGray),
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = colorResource(R.color.white),
    onSurface = Color.White,
    outlineVariant = colorResource(R.color.lightGray),
)

@Composable
private fun lightColorScheme() = MaterialTheme.colorScheme.copy(
    primary = Color(0xFF6200EE),
    primaryContainer = Color(0xFF3700B3),
    secondary = Color(0xFF03DAC5),
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = colorResource(R.color.black),
    onSurface = Color.Black,
    outlineVariant = colorResource(R.color.darkGray)
)

@Composable
fun TennisScoreboardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = when {
        darkTheme -> darkColors()
        else -> lightColors()
    }

    CompositionLocalProvider(LocalColors provides colors) {
        MaterialTheme(
            colorScheme = when {
                darkTheme -> darkColorScheme()
                else -> lightColorScheme()
            },
            typography = MaterialTheme.typography.copy(
                displayLarge = MaterialTheme.typography.displayLarge.copy(
                    color = LocalColors.current.text
                ),
                displayMedium = MaterialTheme.typography.displayMedium.copy(
                    color = LocalColors.current.text
                ),
                displaySmall = MaterialTheme.typography.displaySmall.copy(
                    color = LocalColors.current.text
                ),
                headlineLarge = MaterialTheme.typography.headlineLarge.copy(
                    color = LocalColors.current.text
                ),
                headlineMedium = MaterialTheme.typography.headlineMedium.copy(
                    color = LocalColors.current.text
                ),
                headlineSmall = MaterialTheme.typography.headlineSmall.copy(
                    color = LocalColors.current.text
                ),
                titleLarge = MaterialTheme.typography.titleLarge.copy(
                    color = LocalColors.current.text
                ),
                titleMedium = MaterialTheme.typography.titleMedium.copy(
                    color = LocalColors.current.text
                ),
                titleSmall = MaterialTheme.typography.titleSmall.copy(
                    color = LocalColors.current.text
                ),
                bodyLarge = MaterialTheme.typography.bodyLarge.copy(
                    color = LocalColors.current.text
                ),
                bodyMedium = MaterialTheme.typography.bodyMedium.copy(
                    color = LocalColors.current.text
                ),
                bodySmall = MaterialTheme.typography.bodySmall.copy(
                    color = LocalColors.current.text
                ),
                labelLarge = MaterialTheme.typography.labelLarge.copy(
                    color = LocalColors.current.text
                ),
                labelMedium = MaterialTheme.typography.labelMedium.copy(
                    color = LocalColors.current.text
                ),
                labelSmall = MaterialTheme.typography.labelSmall.copy(
                    color = LocalColors.current.text
                )
            ),
            content = content
        )
    }
}

val MaterialTheme.Colors: Colors
    @Composable
    @ReadOnlyComposable
    get() = LocalColors.current