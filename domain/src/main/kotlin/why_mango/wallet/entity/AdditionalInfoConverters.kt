package why_mango.wallet.entity

import io.r2dbc.postgresql.codec.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.serializer
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.*

@WritingConverter
object AdditionalInfoWritingConverter : Converter<UpbitAdditionalInfo, Json> {
    override fun convert(source: UpbitAdditionalInfo): Json {
        val format = kotlinx.serialization.json.Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        }

        return Json.of(format.encodeToString(source))
    }
}

@ReadingConverter
object AdditionalInfoReadingConverter : Converter<Json, AdditionalInfo> {
    override fun convert(source: Json): AdditionalInfo {
        val format = kotlinx.serialization.json.Json {
            isLenient = true
        }
        val serializer = format.serializersModule.serializer<AdditionalInfo>()
        val result = format.decodeFromString(serializer, source.asString())
        return result
    }
}