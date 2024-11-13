package why_mango.enums

import kotlinx.serialization.*

@Serializable
enum class Side{
    @SerialName("bid") BID,
    @SerialName("ask") ASK,
}