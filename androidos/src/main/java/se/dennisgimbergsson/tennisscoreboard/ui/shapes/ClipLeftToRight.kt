package se.dennisgimbergsson.tennisscoreboard.ui.shapes

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class ClipLeftToRight(private val progress: Float) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val width = size.width
            val height = size.height
            val clipWidth = width * progress

            moveTo(0f, 0f)
            lineTo(clipWidth, 0f)
            lineTo(clipWidth, height)
            lineTo(0f, height)
            close()
        }
        return Outline.Generic(path)
    }
}