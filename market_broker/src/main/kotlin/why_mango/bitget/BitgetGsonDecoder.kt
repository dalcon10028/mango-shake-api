package why_mango.bitget

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import feign.Response
import feign.codec.Decoder
import java.io.InputStreamReader
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class BitgetGsonDecoder(private val gson: Gson) : Decoder {
    override fun decode(response: Response, type: Type): Any? {
        response.body()?.asInputStream().use {
            val reader = it?.let { it1 -> InputStreamReader(it1) }

            return if (type is ParameterizedType) {
                val rawType = type.rawType as Class<*>
                val actualType = type.actualTypeArguments.firstOrNull() ?: Any::class.java
                val targetType: Type = TypeToken.getParameterized(rawType, actualType).type

                gson.fromJson(reader, targetType)
            } else {
                gson.fromJson(reader, type)
            }
        }
    }
}