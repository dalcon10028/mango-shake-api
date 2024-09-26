package why_mango.upbit

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.wiremock.ListenerMode
import io.kotest.extensions.wiremock.WireMockListener
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
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
})