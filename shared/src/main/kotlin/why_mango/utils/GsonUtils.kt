package why_mango.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

val gson = Gson()

// https://stackoverflow.com/questions/49860916/how-to-convert-a-kotlin-data-class-object-to-map

//convert a data class to a map
fun <T> T.serializeToMap(): Map<String, Any> {
    return convert()
}

//convert a map to a data class
inline fun <reified T> Map<String, Any>.toDataClass(): T {
    return convert()
}

//convert an object of type I to type O
inline fun <I, reified O> I.convert(): O {
    val json = gson.toJson(this)
    return gson.fromJson(json, object : TypeToken<O>() {}.type)
}