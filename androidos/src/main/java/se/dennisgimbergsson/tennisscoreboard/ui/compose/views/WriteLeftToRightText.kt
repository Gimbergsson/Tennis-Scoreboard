package se.dennisgimbergsson.tennisscoreboard.ui.compose.views

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.TextStyle
import se.dennisgimbergsson.shared.utils.ThemedPreview
import se.dennisgimbergsson.tennisscoreboard.ui.shapes.ClipLeftToRight
import se.dennisgimbergsson.tennisscoreboard.ui.compose.theme.TennisScoreboardTheme

@Composable
fun WriteLeftToRightText(
    text: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.headlineLarge,
    animationProgress: Float = 0f,
) {
    var currentText by remember { mutableStateOf(text) }
    var animationProgress by remember { mutableFloatStateOf(animationProgress) }

    LaunchedEffect(text) {
        if (text != currentText) {
            currentText = text
            val animationSpec = tween<Float>(
                durationMillis = 500,
                easing = LinearEasing
            )

            Animatable(0f).animateTo(
                targetValue = 1f,
                animationSpec = animationSpec
            ) {
                animationProgress = value
            }
        }
    }

    Box(modifier = modifier.clipToBounds()) {
        Text(
            text = currentText,
            style = textStyle,
            modifier = Modifier
                .align(Alignment.Center)
                .clip(ClipLeftToRight(animationProgress))
            /*.graphicsLayer {
                alpha = animationProgress
            }*/
        )
    }
}


@ThemedPreview
@Composable
private fun MorphingTextPreview() = TennisScoreboardTheme {
    WriteLeftToRightText(
        text = "123",
        animationProgress = 100f,
    )
}