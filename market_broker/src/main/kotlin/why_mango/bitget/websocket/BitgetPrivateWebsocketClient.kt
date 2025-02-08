package why_mango.bitget.websocket

import java.util.*
import kotlinx.coroutines.flow.*
import why_mango.bitget.dto.websocket.*
import why_mango.bitget.enums.WebsocketAction.*
import why_mango.bitget.dto.websocket.push_event.*
import why_mango.bitget.enums.HistoryPositionChannel.*

import com.google.gson.JsonElement
import why_mango.bitget.enums.WebsocketAction
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import why_mango.bitget.AbstractBitgetWebsocketClient
import why_mango.bitget.config.BitgetProperties
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
    private val _historyPositionSharedFlow = MutableSharedFlow<HistoryPositionPushEvent>(replay = 200)
    private val mac: Mac = Mac.getInstance("HmacSHA256").also {
        it.init(
            bitgetProperties.secretKey.toByteArray(charset("UTF-8"))
                .let { sec -> SecretKeySpec(sec, "HmacSHA256") }
        )
    }

    val historyPositionEventFlow = _historyPositionSharedFlow.asSharedFlow()

    override fun subscriptionMessage(): BitgetSubscribeRequest = subscribeChannels {
        channel(
            channel = HISTORY_POSITION,
            instId = "default"
        )
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

    override fun handleMessage(channel: String, action: WebsocketAction, json: JsonElement?) {
        when {
            channel == HISTORY_POSITION.value && action == UPDATE -> {
                parseJson<List<HistoryPositionPushEvent>>(json).forEach {
                    _historyPositionSharedFlow.tryEmit(it)
                }
            }
            channel == HISTORY_POSITION.value && action == SNAPSHOT -> {}
            else -> {
                logger.warn { "Unknown channel: $channel" }
            }
        }
    }
}