package why_mango.upbit.dto

import kotlinx.serialization.Serializable
import why_mango.enums.Currency
import why_mango.serializer.BigDecimalSerializer
import why_mango.serializer.DateTimeSerializer
import java.math.BigDecimal
import java.time.LocalDateTime

// https://docs.upbit.com/reference/%EC%9D%BCday-%EC%BA%94%EB%93%A4-1

/**
 * market	마켓 코드 (ex. KRW-BTC)	String
 * to	마지막 캔들 시각 (exclusive).
 * ISO8061 포맷 (yyyy-MM-dd'T'HH:mm:ss'Z' or yyyy-MM-dd HH:mm:ss). 기본적으로 UTC 기준 시간이며 2023-01-01T00:00:00+09:00 과 같이 KST 시간으로 요청 가능.
 * 비워서 요청시 가장 최근 캔들	String
 * count	캔들 개수(최대 200개까지 요청 가능)	Integer
 * convertingPriceUnit	종가 환산 화폐 단위 (생략 가능, KRW로 명시할 시 원화 환산 가격을 반환.)	String
 */

data class CandleDayQuery(
    /* 마켓 코드 (ex. KRW-BTC) */
    val market: String,

    /* 마지막 캔들 시각 (exclusive) */
    val to: LocalDateTime? = null,

    /* 캔들 개수(최대 200개까지 요청 가능) */
    val count: Int? = 200,

    /* 종가 환산 화폐 단위 (생략 가능, KRW로 명시할 시 원화 환산 가격을 반환.) */
    val convertingPriceUnit: Currency? = Currency.KRW,
) {
    init {
        require(market.isNotEmpty()) { "market should not be empty" }
        require(count == null || count in 1..200) { "count should be between 1 and 200" }
    }
}


/**
 * market	종목 코드	String
 * candle_date_time_utc	캔들 기준 시각(UTC 기준)
 * 포맷: yyyy-MM-dd'T'HH:mm:ss	String
 * candle_date_time_kst	캔들 기준 시각(KST 기준)
 * 포맷: yyyy-MM-dd'T'HH:mm:ss	String
 * opening_price	시가	Double
 * high_price	고가	Double
 * low_price	저가	Double
 * trade_price	종가	Double
 * timestamp	마지막 틱이 저장된 시각	Long
 * candle_acc_trade_price	누적 거래 금액	Double
 * candle_acc_trade_volume	누적 거래량	Double
 * prev_closing_price	전일 종가(UTC 0시 기준)	Double
 * change_price	전일 종가 대비 변화 금액	Double
 * change_rate	전일 종가 대비 변화량	Double
 * converted_trade_price	종가 환산 화폐 단위로 환산된 가격(요청에 convertingPriceUnit 파라미터 없을 시 해당 필드 포함되지 않음.)	Double
 */
@Serializable
data class CandleDayResponse(
    /* 종목 코드 */
    val market: String,

    /* 캔들 기준 시각(UTC 기준) */
    @Serializable(with = DateTimeSerializer::class)
    val candleDateTimeUtc: LocalDateTime,

    /* 캔들 기준 시각(KST 기준) */
    @Serializable(with = DateTimeSerializer::class)
    val candleDateTimeKst: LocalDateTime,

    /* 시가 */
    @Serializable(with = BigDecimalSerializer::class)
    val openingPrice: BigDecimal,

    /* 고가 */
    @Serializable(with = BigDecimalSerializer::class)
    val highPrice: BigDecimal,

    /* 저가 */
    @Serializable(with = BigDecimalSerializer::class)
    val lowPrice: BigDecimal,

    /* 종가 */
    @Serializable(with = BigDecimalSerializer::class)
    val tradePrice: BigDecimal,

    /* 마지막 틱이 저장된 시각 */
    val timestamp: Long,

    /* 누적 거래 금액 */
    @Serializable(with = BigDecimalSerializer::class)
    val candleAccTradePrice: BigDecimal,

    /* 누적 거래량 */
    @Serializable(with = BigDecimalSerializer::class)
    val candleAccTradeVolume: BigDecimal,

    /* 전일 종가(UTC 0시 기준) */
    @Serializable(with = BigDecimalSerializer::class)
    val prevClosingPrice: BigDecimal,

    /* 전일 종가 대비 변화 금액 */
    @Serializable(with = BigDecimalSerializer::class)
    val changePrice: BigDecimal?,

    /* 전일 종가 대비 변화량 */
    @Serializable(with = BigDecimalSerializer::class)
    val changeRate: BigDecimal?,

    /* 종가 환산 화폐 단위로 환산된 가격(요청에 convertingPriceUnit 파라미터 없을 시 해당 필드 포함되지 않음.) */
    @Serializable(with = BigDecimalSerializer::class)
    val convertedTradePrice: BigDecimal? = null,
)