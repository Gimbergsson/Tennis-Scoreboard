package se.dennisgimbergsson.shared

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import se.dennisgimbergsson.shared.enums.GameScores
import java.lang.reflect.Type

class GameScoresDeserializer : JsonDeserializer<GameScores> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): GameScores {
        val stringValue = json?.asString
        return GameScores.entries.find { it.name == stringValue } ?: GameScores.ZERO
    }
}