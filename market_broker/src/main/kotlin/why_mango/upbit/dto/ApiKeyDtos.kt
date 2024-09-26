package why_mango.upbit.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiKeyResponse(
    val accessKey: String,
    val expireAt: String
)