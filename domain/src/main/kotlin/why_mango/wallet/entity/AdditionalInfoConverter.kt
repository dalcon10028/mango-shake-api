package why_mango.wallet.entity

import io.r2dbc.postgresql.codec.Json
import kotlinx.serialization.encodeToString
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import why_mango.wallet.AdditionalInfo

@WritingConverter
object AdditionalInfoWritingConverter : Converter<AdditionalInfo, Json> {
    override fun convert(source: AdditionalInfo): Json {
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
            ignoreUnknownKeys = true
        }
        val result = format.decodeFromString<AdditionalInfo>(source.asString())
        return result
    }
}