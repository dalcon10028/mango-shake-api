package why_mango.ohlcv

import io.kotest.core.spec.style.FunSpec
import org.junit.jupiter.api.Assertions.*
import org.springframework.boot.test.context.SpringBootTest
import why_mango.enums.Currency
import why_mango.enums.Exchange
import java.math.BigDecimal
import java.time.LocalDate

@SpringBootTest
class OhlcvDayServiceTest(
    private val ohlcvDayService: OhlcvDayService
) : FunSpec({
    test("createOhlcvDay") {
        val ohlcvDayCreate = OhlcvDayCreate(
            symbol = "SOL/KRW",
            exchange = Exchange.UPBIT,
            baseDate = LocalDate.now(),
            currency = Currency.KRW,
            open = BigDecimal(100.0),
            high = BigDecimal(200.0),
            low = BigDecimal(50.0),
            close = BigDecimal(150.0),
            volume = BigDecimal(1000.0),
        )
        val ohlcvDayModel = ohlcvDayService.createOhlcvDay(ohlcvDayCreate)
        assertEquals(ohlcvDayModel.symbol, ohlcvDayCreate.symbol)
        assertEquals(ohlcvDayModel.open, ohlcvDayCreate.open)
        assertEquals(ohlcvDayModel.high, ohlcvDayCreate.high)
        assertEquals(ohlcvDayModel.low, ohlcvDayCreate.low)
        assertEquals(ohlcvDayModel.close, ohlcvDayCreate.close)
        assertEquals(ohlcvDayModel.volume, ohlcvDayCreate.volume)
    }
})