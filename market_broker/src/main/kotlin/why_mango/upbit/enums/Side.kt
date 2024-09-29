package why_mango.upbit.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Side {
    @SerialName("ask") ASK,
    @SerialName("bid") BID,
}