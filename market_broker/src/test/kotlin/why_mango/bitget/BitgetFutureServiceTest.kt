package why_mango.bitget

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldHave
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import org.springframework.boot.test.context.SpringBootTest
import why_mango.bitget.dto.history_candle_stick.HistoryCandlestickQuery
import why_mango.bitget.enums.Granularity

@SpringBootTest(
    classes = [BitgetFutureService::class, BitgetRest::class, BitgetFeignConfig::class]
)
class BitgetFutureServiceTest(
    private val bitgetFutureService: BitgetFutureService,
) : FunSpec({
    test("getHistoryCandlestick") {
        val query = HistoryCandlestickQuery(
            symbol = "BTCUSDT",
            granularity = Granularity.ONE_MINUTE.value,
            limit = 10
        )

        val historyCandlestick = bitgetFutureService.getHistoryCandlestick(query).toList()

        historyCandlestick.size shouldBe 10
    }
})