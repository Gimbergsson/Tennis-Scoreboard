package se.dennisgimbergsson.tennisscoring.data.codec

import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import se.dennisgimbergsson.tennisscoring.data.Points
import java.lang.reflect.Type

class PointsSerializer : JsonSerializer<Points> {

    override fun serialize(
        point: Points?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?,
    ) = point?.let {
        JsonPrimitive(it.name)
    }
}