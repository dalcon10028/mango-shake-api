package why_mango.wallet.entity

import io.r2dbc.postgresql.codec.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import why_mango.wallet.AdditionalInfo
import why_mango.wallet.UpbitAdditionalInfo

private val format = kotlinx.serialization.json.Json {
    prettyPrint = true
    isLenient = true
    ignoreUnknownKeys = true
    serializersModule = SerializersModule {
        polymorphic(AdditionalInfo::class) {
            subclass(UpbitAdditionalInfo::class)
            defaultDeserializer { UpbitAdditionalInfo.serializer()}
        }
    }
}

@WritingConverter
object AdditionalInfoWritingConverter : Converter<AdditionalInfo, Json> {
    override fun convert(source: AdditionalInfo): Json {
        return Json.of(format.encodeToString(source))
    }
}

@ReadingConverter
object AdditionalInfoReadingConverter : Converter<Json, AdditionalInfo> {
    override fun convert(source: Json): AdditionalInfo {
        return format.decodeFromString<AdditionalInfo>(source.asString())
    }
}