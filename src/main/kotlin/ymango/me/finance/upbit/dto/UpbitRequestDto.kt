package ymango.me.finance.upbit.dto

import kotlinx.serialization.*

@Serializable
data class UpbitApiKey(
    @SerialName("access_key")
    val accessKey: String,

    @SerialName("expire_at")
    val expireAt: String,
)