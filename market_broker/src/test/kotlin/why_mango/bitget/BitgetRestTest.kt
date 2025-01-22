package why_mango.bitget

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.wiremock.ListenerMode
import io.kotest.extensions.wiremock.WireMockListener
import io.kotest.matchers.*
import org.springframework.boot.test.context.SpringBootTest
import why_mango.bitget.dto.market.HistoryCandlestickQuery
import why_mango.bitget.dto.position.AllPositionsQuery
import why_mango.bitget.enums.*

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

    test("getAllPositions") {
        // {"code":"00000","msg":"success","requestTime":1737339766320,"data":[{"marginCoin":"SUSDT","symbol":"SBTCSUSDT","holdSide":"long","openDelegateSize":"0","marginSize":"161.56128","available":"0.016","locked":"0","total":"0.016","leverage":"10","achievedProfits":"0","openPriceAvg":"100975.8","marginMode":"isolated","posMode":"hedge_mode","unrealizedPL":"5.3536","liquidationPrice":"91279.851345922058","keepMarginRate":"0.004","markPrice":"101310.4","marginRatio":"0.04272987621","breakEvenPrice":"101036.409723889556","totalFee":"","deductedFee":"0.32312256","grant":"","assetMode":"single","autoMargin":"off","takeProfit":"","stopLoss":"","takeProfitId":"","stopLossId":"","cTime":"1737339040134","uTime":"1737339040134"}]}
        val query = AllPositionsQuery(
            productType = ProductType.SUSDT_FUTURES,
        )
        val response = bitgetRest.getAllPositions(query)

        println(response)
    }

//    test("placeOrder") {
//        val body = PlaceOrderRequest(
//            symbol = "BTCUSDT",
//            marginCoin = "USDT",
//            size = BigDecimal("0.1"),
//            productType = ProductType.USDT_FUTURES,
//            side = Side.SELL,
//            orderType = OrderType.LIMIT,
//            tradeSide = TradeType.OPEN,
//            marginMode = MarginMode.ISOLATED,
//            presetStopSurplusPrice = BigDecimal("2000"),
//            presetStopLossPrice = BigDecimal("1900"),
//        )
//        val order = bitgetRest.placeOrder(body)
//
//        println(order)
//    }
})