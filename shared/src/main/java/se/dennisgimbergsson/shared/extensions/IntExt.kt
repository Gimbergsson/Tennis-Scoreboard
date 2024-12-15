package se.dennisgimbergsson.shared.extensions

import android.content.Context
import android.content.res.Resources
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

/**
 * This converts dp unit to equivalent pixels, depending on device density.
 */
val Int.toPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

/**
 * This converts device specific pixels to density independent pixels.
 */
val Int.toDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

/**
 * This gets the specified color in int format from a color resource.
 */
@ColorInt
fun Int.toColorInt(context: Context) =
    ResourcesCompat.getColor(context.resources, this, context.theme)

/**
 * This gets the specified color in hex format from a color resource.
 */
fun Int.toColorHex(context: Context) =
    "#" + Integer.toHexString(ContextCompat.getColor(context, this))
        .substring(3) // Substring removes alpha channel e.g. "FF00B2FF" -> "00B2FF"

/**
 * This gets the specified drawable resource as a drawable object.
 */
fun Int.toDrawableRes(context: Context) =
    ResourcesCompat.getDrawable(context.resources, this, context.theme)

/**
 * This takes an integer and separates it into groups of 3 digits as a string.
 */
fun Int.toChunksString() = toString().chunked(3).joinToString(separator = " ")