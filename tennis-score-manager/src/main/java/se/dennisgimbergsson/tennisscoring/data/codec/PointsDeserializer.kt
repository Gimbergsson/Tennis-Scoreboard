package se.dennisgimbergsson.tennisscoring.data.codec

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import se.dennisgimbergsson.tennisscoring.data.Points
import java.lang.reflect.Type

class PointsDeserializer : JsonDeserializer<Points> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?,
    ) = Points.entries.find { it.name == json?.asString } ?: Points.ZERO
}