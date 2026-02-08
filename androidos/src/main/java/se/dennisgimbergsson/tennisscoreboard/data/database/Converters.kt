package se.dennisgimbergsson.tennisscoreboard.data.database

import android.util.Log
import androidx.room.TypeConverter
import com.google.gson.Gson
import se.dennisgimbergsson.tennisscoreboard.data.entities.TeamScore

class Converters {
    @TypeConverter
    fun fromScore(teamScore: TeamScore?): String? {
        return Gson().toJson(teamScore) // Convert Score to a JSON string
    }

    @TypeConverter
    fun toScore(scoreString: String?): TeamScore? {
        return try {
            Gson().fromJson(scoreString, TeamScore::class.java) // Convert JSON string back to Score
        } catch (e: Exception) {
            Log.e(TAG, "Error converting Score from JSON", e)
            null // Handle potential parsing errors
        }
    }

    companion object {
        private const val TAG = "Converters"
    }
}