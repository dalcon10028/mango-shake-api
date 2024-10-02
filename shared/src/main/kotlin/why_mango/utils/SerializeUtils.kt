package why_mango.utils

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val format = Json {
    prettyPrint = true
    isLenient = true
    ignoreUnknownKeys = true
}

fun Any.serialize(): String {
    return format.encodeToString(this)
}

inline fun <reified T> String.deserialize(clazz: Class<T>): T {
    return format.decodeFromString(this)
}