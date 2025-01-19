package why_mango.upbit

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.wiremock.ListenerMode
import io.kotest.extensions.wiremock.WireMockListener
import io.kotest.matchers.shouldBe
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import org.springframework.boot.test.context.SpringBootTest
import why_mango.upbit.dto.OrderRequestDto
import why_mango.upbit.enums.OrderType
import why_mango.upbit.enums.Side
import java.math.BigDecimal
import java.util.UUID

@SpringBootTest(
    classes = [UpbitRest::class, UpbitFeignConfig::class]
)
class UpbitRestTest(
    private val upbitRest: UpbitRest
) : FunSpec({
    val mockserver = WireMockServer(9000)
    listener(WireMockListener(mockserver, ListenerMode.PER_SPEC))

    test("getApiKeys") {
        val accessKey = "test_access_key"
        val expireAt = "test_expire_at"

        mockserver.stubFor(
            get(urlPathMatching("/v1/api_keys"))
                .willReturn(
                    aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"access_key\":\"$accessKey\",\"expire_at\":\"$expireAt\"}]")
                )
        )

        val apiKeys = upbitRest.getApiKeys()

        apiKeys.size shouldBe 1
        apiKeys[0].accessKey shouldBe accessKey
        apiKeys[0].expireAt shouldBe expireAt
    }

    test("getAccounts") {
        val currency = "BTC"
        val balance = BigDecimal("0.00320325")
        val locked = BigDecimal("0")
        val avgBuyPrice = BigDecimal("86090384.84039647")
        val avgBuyPriceModified = false
        val unitCurrency = "KRW"

        mockserver.stubFor(
            get(urlPathMatching("/v1/accounts"))
                .willReturn(
                    aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            [
                                {
                                    "currency": "$currency",
                                    "balance": "$balance",
                                    "locked": "$locked",
                                    "avg_buy_price": "$avgBuyPrice",
                                    "avg_buy_price_modified": $avgBuyPriceModified,
                                    "unit_currency": "$unitCurrency"
                                }
                            ]
                        """.trimIndent())
                )
        )

        val accounts = upbitRest.getAccounts("Bearer test_token")

        accounts.size shouldBe 1
        accounts[0].currency shouldBe currency
        accounts[0].balance shouldBe balance
        accounts[0].locked shouldBe locked
        accounts[0].avgBuyPrice shouldBe avgBuyPrice
        accounts[0].avgBuyPriceModified shouldBe avgBuyPriceModified
        accounts[0].unitCurrency shouldBe unitCurrency
    }

    test("order") {
        val body = OrderRequestDto(
            market = "KRW-BTC",
            side = Side.ASK,
            volume = BigDecimal("0.00032032"),
            price = BigDecimal("88271000"),
            ordType = OrderType.LIMIT,
            identifier = UUID.randomUUID().toString()
        )

        upbitRest.order(body)
    }

    test("serialize") {
        val body = OrderRequestDto(
            market = "KRW-BTC",
            side = Side.ASK,
            volume = BigDecimal("0.00032032"),
            price = BigDecimal("88271000"),
            ordType = OrderType.LIMIT,
            identifier = UUID.randomUUID().toString()
        )
        @OptIn(ExperimentalSerializationApi::class)
        val format = Json {
            namingStrategy = JsonNamingStrategy.SnakeCase
            decodeEnumsCaseInsensitive = true
            explicitNulls = false
        }
        println(format.encodeToString(body))
    }
})