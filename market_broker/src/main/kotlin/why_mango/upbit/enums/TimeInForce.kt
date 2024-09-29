package why_mango.upbit.enums

import kotlinx.serialization.*

@Serializable
enum class TimeInForce {
    @SerialName("ioc") IMMEDIATE_OR_CANCEL,
    @SerialName("fok") FILL_OR_KILL
}