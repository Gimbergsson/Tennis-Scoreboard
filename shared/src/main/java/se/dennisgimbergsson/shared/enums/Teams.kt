package se.dennisgimbergsson.shared.enums

enum class Teams {
    HOME,
    AWAY

    /*companion object {
        fun fromString(value: String): Teams = entries.firstOrNull {
            it.name.equals(value, ignoreCase = true)
        } ?: throw IllegalArgumentException("Invalid value: $value")
    }*/
}