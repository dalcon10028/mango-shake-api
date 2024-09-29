package why_mango.upbit.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpbitErrorResponse(
    val error: UpbitError
)

@Serializable
data class UpbitError(
    val message: String, // 오류에 대한 설명
    val name: String // 오류 코드
)