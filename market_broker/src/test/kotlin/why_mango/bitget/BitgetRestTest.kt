package why_mango.bitget

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.wiremock.ListenerMode
import io.kotest.extensions.wiremock.WireMockListener
import io.kotest.matchers.*
import org.springframework.boot.test.context.SpringBootTest
import why_mango.bitget.dto.history_candle_stick.HistoryCandlestickQuery
import why_mango.bitget.enums.Granularity

@SpringBootTest(
    classes = [BitgetRest::class, BitgetFeignConfig::class]
)
class BitgetRestTest(
    private val bitgetRest: BitgetRest
) : FunSpec({
    val mockserver = WireMockServer(9000)
    listener(WireMockListener(mockserver, ListenerMode.PER_SPEC))

    test("getHistoryCandlestick") {
        mockserver.stubFor(
            get(urlPathMatching("/api/v2/mix/market/history-candles"))
                .willReturn(
                    aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "code": "00000",
                                "msg": "success",
                                "requestTime": 1737292876442,
                                "data": [
                                    ["1737292260000", "105191.2", "105191.2", "105108.3", "105149.9", "226.934", "23860656.2146"],
                                    ["1737292320000", "105149.9", "105258", "105130", "105258", "192.965", "20295610.3594"],
                                    ["1737292380000", "105258", "105258", "105156.8", "105168.5", "226.592", "23841188.4582"],
                                    ["1737292440000", "105168.5", "105190.7", "105149.9", "105190.7", "81.155", "8535012.2314"],
                                    ["1737292500000", "105190.7", "105190.7", "105133.2", "105183.6", "105.283", "11071236.4705"],
                                    ["1737292560000", "105183.6", "105246.5", "105149.4", "105242.5", "76.305", "8027097.4987"],
                                    ["1737292620000", "105242.5", "105323.1", "105210.3", "105321.2", "124.331", "13088827.9465"],
                                    ["1737292680000", "105321.2", "105321.5", "105268.4", "105268.4", "213.262", "22451005.1384"],
                                    ["1737292740000", "105268.4", "105268.6", "105170.7", "105199.9", "85.282", "8973085.7837"],
                                    ["1737292800000", "105199.9", "105219.7", "105141", "105159.9", "104.071", "10947197.7559"]
                                ]
                            }
                        """.trimIndent())
                )
        )

        val query = HistoryCandlestickQuery(
            symbol = "BTCUSDT",
            granularity = Granularity.ONE_MINUTE.value,
            limit = 10
        )
        val historyCandlestick = bitgetRest.getHistoryCandlestick(query)

        historyCandlestick.code shouldBe "00000"
        historyCandlestick.msg shouldBe "success"
        historyCandlestick.requestTime shouldBe 1737292876442
        historyCandlestick.data.size shouldBe 10
    }
})