package why_mango.serialization.gson

import com.google.gson.*
import java.lang.reflect.Type
import java.math.BigDecimal
import kotlin.jvm.Throws

object NumberStringSerializer : JsonSerializer<BigDecimal>, JsonDeserializer<BigDecimal> {
    override fun serialize(src: BigDecimal, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.toString())
    }

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): BigDecimal {
        return try {
            when {
                json.isJsonPrimitive -> {
                    val primitive = json.asJsonPrimitive
                    when {
                        primitive.isNumber -> primitive.asBigDecimal // ✅ Handles actual numbers
                        primitive.isString -> BigDecimal(
                            when {
                                primitive.asString.isBlank() -> "0" // ✅ Handles empty strings
                                else -> primitive.asString.replace(",", "") // ✅ Handles commas
                            }
                        )
                        else -> throw JsonParseException("Cannot parse to BigDecimal: $json")
                    }
                }

                else -> throw JsonParseException("Invalid BigDecimal format: $json")
            }
        } catch (e: NumberFormatException) {
            throw JsonParseException(e)
        }
    }
}