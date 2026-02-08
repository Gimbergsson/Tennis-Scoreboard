package se.dennisgimbergsson.tennisscoreboard.ui.compose.views


import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import se.dennisgimbergsson.shared.utils.ThemedPreview
import se.dennisgimbergsson.tennisscoreboard.ui.compose.theme.TennisScoreboardTheme

@Composable
fun FlipNumberText(
    text: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current.copy(
        textAlign = TextAlign.Center
    )
) {
    var newText by remember { mutableStateOf(text) }
    var animatingText by remember { mutableStateOf(text) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var targetValue by remember { mutableFloatStateOf(0f) }

    // Key the LaunchedEffect on the incoming 'number' prop
    LaunchedEffect(text) {
        // Do an early return if the newText is the same as the text
        if (text == newText) return@LaunchedEffect
        newText = text

        targetValue = if (animatingText == "Adv." && text == "40") {
            -180f
        } else {
            180f
        }
        // Start the animation
        Animatable(0f)
            .animateTo(
                targetValue = targetValue,
                animationSpec = tween<Float>(
                    durationMillis = 750, // Adjust duration as needed
                    easing = LinearEasing
                )
            ) {
                rotation = value
            }

        animatingText = newText // Update the current number *after* the animation
        rotation = 0f // Reset rotation for next animation
    }

    val front = targetValue == 180f && rotation < 90f
    val back = targetValue == -180f && rotation > -90f

    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                rotationX = rotation
                cameraDistance = 12f * density  // Add perspective
                transformOrigin = TransformOrigin(0.5f, 0.5f) //Ensure rotation around center.
            },
        contentAlignment = Alignment.Center
    ) {
        when {
            front -> {
                /*Text(
                    modifier = Modifier
                        .align(Alignment.Center),
                    text = currentText,
                    style = textStyle, //Added a style parameter.
                )*/
                /*AutoResizedText(
                    modifier = Modifier
                        .align(Alignment.Center),
                    style = textStyle.copy(
                        textAlign = TextAlign.Center
                    ),
                    text = currentText
                )*/
                BasicText(
                    modifier = Modifier,
                    style = textStyle,
                    text = animatingText,
                    softWrap = false,
                    autoSize = TextAutoSize.StepBased(16.sp, 300.sp),
                )
            }
            back -> {
                /*BasicText(
                    modifier = Modifier,
                    style = textStyle,
                    text = animatingText,
                    softWrap = false,
                    autoSize = TextAutoSize.StepBased(16.sp, 300.sp),
                )*/
                BasicText(
                    modifier = Modifier
                        .graphicsLayer {
                            rotationX = 0f
                        },
                    style = textStyle,
                    text = animatingText,
                    softWrap = false,
                    autoSize = TextAutoSize.StepBased(16.sp, 300.sp),
                )
            }
            else -> {
                // Display the new number, rotated 180 degrees (so it's facing the right way)
                /*Text(
                    text = text,
                    style = textStyle, //Added a style parameter.
                    modifier = Modifier
                        .align(Alignment.Center)
                        .graphicsLayer {
                            rotationX = 180f
                        }
                )*/
                /*AutoResizedText(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .graphicsLayer {
                            rotationX = 180f
                        },
                    style = textStyle.copy(
                        textAlign = TextAlign.Center
                    ),
                    text = currentText
                )*/
                BasicText(
                    modifier = Modifier
                        .graphicsLayer {
                            rotationX = targetValue
                        },
                    style = textStyle,
                    text = text,
                    softWrap = false,
                    autoSize = TextAutoSize.StepBased(16.sp, 300.sp),
                )
            }
        }
    }
}

@ThemedPreview
@Composable
private fun FlipNumberTextFieldPreview() = TennisScoreboardTheme {
    FlipNumberText(
        text = "15",
    )
}
