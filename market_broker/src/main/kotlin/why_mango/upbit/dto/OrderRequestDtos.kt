package why_mango.upbit.dto

import kotlinx.serialization.Serializable
import why_mango.dto.BaseDto
import why_mango.serialization.kserialization.serializer.BigDecimalSerializer
import why_mango.serialization.kserialization.serializer.DateTimeSerializer
import why_mango.upbit.enums.*
import java.math.BigDecimal
import java.time.LocalDateTime

// https://docs.upbit.com/reference/%EC%A3%BC%EB%AC%B8%ED%95%98%EA%B8%B0

/**
 * market *	마켓 ID (필수)	String
 * side *	주문 종류 (필수)
 * - bid : 매수
 * - ask : 매도	String
 * volume *	주문량 (지정가, 시장가 매도 시 필수)	NumberString
 * price *	주문 가격. (지정가, 시장가 매수 시 필수)
 * ex) KRW-BTC 마켓에서 1BTC당 1,000 KRW로 거래할 경우, 값은 1000 이 된다.
 * ex) KRW-BTC 마켓에서 1BTC당 매도 1호가가 500 KRW 인 경우, 시장가 매수 시 값을 1000으로 세팅하면 2BTC가 매수된다.
 * (수수료가 존재하거나 매도 1호가의 수량에 따라 상이할 수 있음)	NumberString
 * ord_type *	주문 타입 (필수)
 * - limit : 지정가 주문
 * - price : 시장가 주문(매수)
 * - market : 시장가 주문(매도)
 * - best : 최유리 주문 (time_in_force 설정 필수)	String
 * identifier	조회용 사용자 지정값 (선택)	String (Uniq 값 사용)
 * time_in_force	IOC, FOK 주문 설정*
 * - ioc : Immediate or Cancel
 * - fok : Fill or Kill
 * *ord_type이 best 혹은 limit 일때만 지원됩니다.	String
 */

@Serializable
data class OrderRequestDto(
    /* 마켓 ID */
    val market: String,

    /* 주문 종류 */
    val side: Side,

    /* 주문량 (지정가, 시장가 매도 시 필수) */
    @Serializable(with = BigDecimalSerializer::class)
    val volume: BigDecimal,

    /* 주문 가격. (지정가, 시장가 매수 시 필수) */
    @Serializable(with = BigDecimalSerializer::class)
    val price: BigDecimal,

    /* 주문 타입 */
    val ordType: OrderType,

    /* 조회용 사용자 지정값 */
    val identifier: String,

    /* IOC, FOK 주문 설정 */
    val timeInForce: TimeInForce? = null,
): BaseDto() {
    init {
        require(ordType != OrderType.BEST || timeInForce != null) { "ordType이 best 일때 timeInForce는 필수입니다." }
    }
}

/**
 * uuid	주문의 고유 아이디	String
 * side	주문 종류	String
 * ord_type	주문 방식	String
 * price	주문 당시 화폐 가격	NumberString
 * state	주문 상태	String
 * market	마켓의 유일키	String
 * created_at	주문 생성 시간	String
 * volume	사용자가 입력한 주문 양	NumberString
 * remaining_volume	체결 후 남은 주문 양	NumberString
 * reserved_fee	수수료로 예약된 비용	NumberString
 * remaining_fee	남은 수수료	NumberString
 * paid_fee	사용된 수수료	NumberString
 * locked	거래에 사용중인 비용	NumberString
 * executed_volume	체결된 양	NumberString
 * trades_count	해당 주문에 걸린 체결 수	Integer
 * time_in_force	IOC, FOK 설정	String
 */

@Serializable
data class OrderRequestResponse(
    /* 주문의 고유 아이디 */
    val uuid: String,

    /* 주문 종류 */
    val side: Side,

    /* 주문 방식 */
    val ordType: OrderType,

    /* 주문 당시 화폐 가격 */
    @Serializable(with = BigDecimalSerializer::class)
    val price: BigDecimal,

    /* 주문 상태 */
    val state: String,

    /* 마켓 ID */
    val market: String,

    /* 주문 생성 시간 */
    @Serializable(with = DateTimeSerializer::class)
    val createdAt: LocalDateTime,

    /* 사용자가 입력한 주문 양 */
    @Serializable(with = BigDecimalSerializer::class)
    val volume: BigDecimal,

    /* 체결 후 남은 주문 양 */
    @Serializable(with = BigDecimalSerializer::class)
    val remainingVolume: BigDecimal,

    /* 수수료로 예약된 비용 */
    @Serializable(with = BigDecimalSerializer::class)
    val reservedFee: BigDecimal,

    /* 남은 수수료 */
    @Serializable(with = BigDecimalSerializer::class)
    val remainingFee: BigDecimal,

    /* 사용된 수수료 */
    @Serializable(with = BigDecimalSerializer::class)
    val paidFee: BigDecimal,

    /* 거래에 사용 중인 비용 */
    @Serializable(with = BigDecimalSerializer::class)
    val locked: BigDecimal,

    /* 체결된 양 */
    @Serializable(with = BigDecimalSerializer::class)
    val executedVolume: BigDecimal,

    /* 해당 주문에 걸린 체결 수 */
    val tradesCount: Int,

    /* IOC, FOK 설정 */
    val timeInForce: TimeInForce? = null,
)