package why_mango.upbit.enums

import kotlinx.serialization.*

@Serializable
enum class OrderType {
    @SerialName("limit") LIMIT, // 지정가 주문
    @SerialName("price") PRICE, // 시장가 주문(매수)
    @SerialName("market") MARKET, // 시장가 주문(매도)
    @SerialName("best") BEST // 최유리 주문
}