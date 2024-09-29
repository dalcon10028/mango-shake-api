package why_mango.ohlcv

import why_mango.enums.Currency
import why_mango.enums.Exchange
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class OhlcvDayCreate(
    val baseDate: LocalDate,
    val exchange: Exchange,
    val currency: Currency,
    val symbol: String,
    val open: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal,
    val close: BigDecimal,
    val volume: BigDecimal,
)

data class OhlcvDayModel(
    val id: Long,
    val baseDate: LocalDate,
    val exchange: Exchange,
    val currency: Currency,
    val symbol: String,
    val open: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal,
    val close: BigDecimal,
    val volume: BigDecimal,
    val createdAt: LocalDateTime,
)