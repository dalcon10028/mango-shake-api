package why_mango.bitget

import com.ninjasquad.springmockk.MockkBean
import com.ninjasquad.springmockk.MockkBeans
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import why_mango.bitget.enums.Granularity
import why_mango.bitget.rest.BitgetDemoFutureService
import why_mango.component.slack.SlackEventListener
import java.util.UUID

@SpringBootTest
@MockkBeans(
    MockkBean(SlackEventListener::class),
    MockkBean(SlackEventListener.SlackProperties::class),
)
class BitgetFutureServiceTest(
    private val bitgetFutureService: BitgetDemoFutureService,
) : FunSpec({
    test("getHistoryCandlestick") {
        val historyCandlestick = bitgetFutureService.getHistoryCandlestick(
            "BTCUSDT", Granularity.ONE_MINUTE, 10
        ).toList()

        historyCandlestick.size shouldBe 10
    }

    test("openLong") {
        // {"code":"00000","msg":"success","requestTime":1737813049609,"data":{"clientOid":"57bb389c-38af-4b11-9f0f-d34526906db3","orderId":"1267045253332500481"}}

        val orderId = UUID.randomUUID().toString()
        val response = bitgetFutureService.openLong(
            symbol = "SXRPSUSDT",
            size = "942".toBigDecimal(),
            price = "3.127".toBigDecimal(),
            presetStopSurplusPrice = "3.130".toBigDecimal(),
            presetStopLossPrice = "3.100".toBigDecimal(),
            orderId = orderId
        )

        response.clientOid shouldBe orderId
    }

    test("close position") {
        // {"code":"00000","msg":"success","requestTime":1737813049609,"data":{"clientOid":"57bb389c-38af-4b11-9f0f-d34526906db3","orderId":"1267045253332500481"}}

        val response = bitgetFutureService.flashClose(
            symbol = "SXRPSUSDT",
        )

        response shouldBe true
    }
})