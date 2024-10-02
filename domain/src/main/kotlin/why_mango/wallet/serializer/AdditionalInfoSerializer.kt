package why_mango.wallet.serializer

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import why_mango.enums.ApiProvider
import why_mango.exception.ErrorCode
import why_mango.exception.MangoShakeException
import why_mango.wallet.entity.AdditionalInfo
import why_mango.wallet.entity.UpbitAdditionalInfo

object AdditionalInfoSerializer : JsonContentPolymorphicSerializer<AdditionalInfo>(AdditionalInfo::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<AdditionalInfo> {
        return when (element.jsonObject["apiProvider"]?.jsonPrimitive?.content) {
            ApiProvider.UPBIT.name -> UpbitAdditionalInfo.serializer()
            else -> throw MangoShakeException(ErrorCode.ILLIGAL_STATE, "Unknown apiProvider")
        }
    }
}