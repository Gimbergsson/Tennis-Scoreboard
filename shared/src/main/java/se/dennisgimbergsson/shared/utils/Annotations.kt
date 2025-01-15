package se.dennisgimbergsson.shared.utils

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.tooling.preview.devices.WearDevices.LARGE_ROUND
import androidx.wear.tooling.preview.devices.WearDevices.RECT
import androidx.wear.tooling.preview.devices.WearDevices.SMALL_ROUND
import androidx.wear.tooling.preview.devices.WearDevices.SQUARE

@Preview(
    name = "Light theme",
    group = "theme",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Dark theme",
    group = "theme",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
annotation class ThemedPreview

@Preview(
    name = "English Locale",
    group = "locale",
    locale = "en"
)
@Preview(
    name = "Swedish Locale",
    group = "locale",
    locale = "sv"
)
@Preview(
    name = "Danish Locale",
    group = "locale",
    locale = "da"
)
@Preview(
    name = "German Locale",
    group = "locale",
    locale = "de"
)
annotation class LocalePreview

@Preview(
    apiLevel = 34,
    device = LARGE_ROUND,
    group = "round",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    apiLevel = 34,
    device = SMALL_ROUND,
    group = "round",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    fontScale = 1.00f,
)
@Preview(
    apiLevel = 34,
    device = RECT,
    group = "rect",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    apiLevel = 34,
    device = SQUARE,
    group = "square",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
annotation class WearPreview
