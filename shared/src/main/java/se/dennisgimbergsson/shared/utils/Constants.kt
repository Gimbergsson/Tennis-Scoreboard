package se.dennisgimbergsson.shared.utils

import java.util.Locale

object Constants {

    object Debug {
        const val WEAR_TAG = "TennisScoreboardWearOS"
        const val ANDROID_TAG = "TennisScoreboardAndroidOS"
    }

    object Keys{
        const val SCOREBOARD = "scoreboard"
        const val SCOREBOARD_HISTORY = "scoreboard_history"
        const val SAVED_SCOREBOARDS = "saved_scoreboards"
        const val SCOREBOARD_UPDATE = "scoreboard_update"
    }

    object Suffixes {
        const val DEVELOPER_SETTINGS_SUFFIX = "_developer_settings"
    }

    object Paths {
        const val SCOREBOARD_UPDATE = "/scoreboard_update"
        const val SAVED_SCOREBOARDS_UPDATE = "/saved_scoreboards_update"
    }

    object Locales {
        val DefaultAppLocale: Locale = Locale.Builder()
            .setLanguage("sv")
            .setRegion("SE")
            .build()
    }

}