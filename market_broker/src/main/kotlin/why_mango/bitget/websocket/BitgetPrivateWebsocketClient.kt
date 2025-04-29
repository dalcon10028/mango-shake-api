package why_mango.bitget.websocket

import java.util.*
import why_mango.bitget.enums.*
import kotlinx.coroutines.flow.*
import why_mango.bitget.dto.websocket.*
import why_mango.bitget.enums.WebsocketAction.*
import why_mango.bitget.dto.websocket.push_event.*
import why_mango.bitget.enums.HistoryPositionChannel.*

import com.google.gson.JsonElement
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import why_mango.bitget.AbstractBitgetWebsocketClient
import why_mango.bitget.config.BitgetProperties
import why_mango.bitget.dto.BitgetWebsocketResponse
import why_mango.bitget.dto.websocket.subscribeChannels
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Component
class BitgetPrivateWebsocketClient(
    private val bitgetProperties: BitgetProperties,
    private val pulisher: ApplicationEventPublisher,
) : AbstractBitgetWebsocketClient(
    baseUrl = bitgetProperties.websocketPrivateUrl,
    pulisher
) {
    enum class InstId {
        SXRPSUSDT, XRPUSDT
    }

    private val mac: Mac = Mac.getInstance("HmacSHA256").also {
        it.init(
            bitgetProperties.secretKey.toByteArray(charset("UTF-8"))
                .let { sec -> SecretKeySpec(sec, "HmacSHA256") }
        )
    }

    private val _sxrpsusdtPositionHistoryChannel = MutableSharedFlow<HistoryPositionPushEvent>(replay = 1)
    val sxrpsusdtPositionHistoryChannel: SharedFlow<HistoryPositionPushEvent> = _sxrpsusdtPositionHistoryChannel.asSharedFlow()

    private val _positionHistoryChannel: Map<String, MutableSharedFlow<HistoryPositionPushEvent>> = mapOf(
        "XRPUSDT" to MutableSharedFlow(replay = 1),
        "DOGEUSDT" to MutableSharedFlow(replay = 1),
        "ETHUSDT" to MutableSharedFlow(replay = 1),
        "SOLUSDT" to MutableSharedFlow(replay = 1),
        "SUIUSDT" to MutableSharedFlow(replay = 1),
    )
    val positionHistoryChannel: Map<String, SharedFlow<HistoryPositionPushEvent>> = _positionHistoryChannel.mapValues { it.value.asSharedFlow() }

    private val _xrpusdtPositionHistoryChannel = MutableSharedFlow<HistoryPositionPushEvent>(replay = 1)
    val xrpusdtPositionHistoryChannel: SharedFlow<HistoryPositionPushEvent> = _xrpusdtPositionHistoryChannel.asSharedFlow()

    override fun subscriptionMessage(): BitgetSubscribeRequest = subscribeChannels {
        channel(
            instType = ProductType.USDT_FUTURES,
            channel = "positions-history",
            instId = "default"
        )
//        channel(
//            instType = ProductType.USDT_FUTURES,
//            channel = HISTORY_POSITION,
//            instId = "default"
//        )
    }

    override fun login(): BitgetLoginRequest? {
        val timestamp = System.currentTimeMillis() / 1000
        val content = "${timestamp}GET/user/verify"
        val sign: String = Base64.getEncoder().encodeToString(mac.doFinal(content.toByteArray(charset("UTF-8"))))

        return BitgetLoginRequest(
            op = "login",
            args = listOf(
                LoginArgs(
                    apiKey = bitgetProperties.accessKey,
                    passphrase = bitgetProperties.passphrase,
                    timestamp = timestamp,
                    sign = sign
                )
            )
        )
    }

    override fun handleMessage(response: BitgetWebsocketResponse<JsonElement>) {
        val channel = HistoryPositionChannel.from(response.arg.channel)
        when (channel) {
            HISTORY_POSITION -> handlePositionHistory(response)
        }
    }

    private fun handlePositionHistory(response: BitgetWebsocketResponse<JsonElement>) {
        when (response.action) {
            UPDATE -> {
                parseJson<List<HistoryPositionPushEvent>>(response.data).forEach {
                    when (it.instId) {
                        "SXRPSUSDT" -> _sxrpsusdtPositionHistoryChannel.tryEmit(it)
                        else -> _positionHistoryChannel[it.instId]?.tryEmit(it)
                    }
                }
            }

            SNAPSHOT -> {}
            else -> {
                logger.warn { "Unknown action: ${response.action}" }
            }
        }
    }
}