package why_mango.bitget.websocket

import why_mango.bitget.enums.*
import kotlinx.coroutines.*
import com.google.gson.*
import why_mango.bitget.dto.websocket.*
import kotlinx.coroutines.flow.*
import okhttp3.*

import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.concurrent.TimeUnit.SECONDS
import com.google.gson.reflect.TypeToken
import okio.ByteString
import okio.EOFException
import org.springframework.stereotype.Component
import why_mango.bitget.config.BitgetProperties
import why_mango.bitget.dto.BitgetWebsocketResponse
import why_mango.bitget.dto.websocket.push_event.CandleStickPushEvent
import why_mango.bitget.dto.websocket.push_event.TickerPushEvent
import why_mango.serialization.gson.NumberStringSerializer
import java.math.BigDecimal

@Component
class BitgetPublicDemoWebsocketClient(
    private val bitgetProperties: BitgetProperties,
) {
    private val logger = KotlinLogging.logger {}
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val client = OkHttpClient().newBuilder()
        .pingInterval(10, SECONDS)
        .build()
    private lateinit var webSocket: WebSocket
    private val gson: Gson = GsonBuilder()
        .enableComplexMapKeySerialization()
        .registerTypeAdapter(BigDecimal::class.java, NumberStringSerializer)
        .create()
    private var isRunning = false
    private val _priceSharedFlow = MutableSharedFlow<TickerPushEvent>(replay = 1)
    private val _candlestickSharedFlow = MutableSharedFlow<CandleStickPushEvent>(replay = 200)
    val priceEventFlow = _priceSharedFlow.asSharedFlow()
    val candlestickEventFlow = _candlestickSharedFlow.asSharedFlow()

    fun connect() {
        val request = Request.Builder()
            .url(bitgetProperties.websocketPublicUrl)
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                logger.info { "Connected to bitget WebSocket" }
                isRunning = true
                startPingJob()

                // 구독 메시지 전송
                val subscribeMessage = subscribeChannels {
                    channel(
                        channel = CandleStickChannel.CANDLE_1MIN,
                        instId = "SXRPSUSDT"
                    )
                    channel(
                        channel = TickerChannel.TICKER,
                        instId = "SXRPSUSDT"
                    )
                }

                webSocket.send(gson.toJson(subscribeMessage))
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                logger.debug { "Received message: $text" }

                if (text == "pong") {
                    logger.debug { "Received pong message" }
                    return
                }

                val baseType = object : TypeToken<BitgetWebsocketResponse<JsonElement>>() {}.type
                val response: BitgetWebsocketResponse<JsonElement> = gson.fromJson(text, baseType)

                response.event?.let {
                    logger.info { "$it event received ${response.arg}" }
                } ?: handleResponse(response.arg.channel, response.data)

            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                logger.debug { "Received bytes: $bytes" }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                logger.info { "Closing WebSocket connection: $code, $reason" }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                logger.info { "Closed WebSocket connection: $code, $reason" }
                reconnect()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                logger.error(t) { "WebSocket failure" }
                isRunning = false

                if (t is EOFException) {
                    logger.warn { "Server closed the WebSocket connection. Attempting to reconnect..." }
                }

                reconnect()
            }
        })
    }

    private fun handleResponse(channel: String, json: JsonElement?) {
        when (channel) {
            CandleStickChannel.CANDLE_1MIN.value,
            CandleStickChannel.CANDLE_5MIN.value,
            CandleStickChannel.CANDLE_15MIN.value,
            CandleStickChannel.CANDLE_30MIN.value,
            CandleStickChannel.CANDLE_1HOUR.value,
            -> {
                val candlestickType = object : TypeToken<List<List<String>>>() {}.type
                gson.fromJson<List<List<String>>>(json, candlestickType)
                    .map { CandleStickPushEvent.from(it) }
                    .forEach { _candlestickSharedFlow.tryEmit(it) }
            }

            TickerChannel.TICKER.value -> {
                val tickerType = object : TypeToken<List<TickerPushEvent>>() {}.type
                gson.fromJson<List<TickerPushEvent>>(json, tickerType)
                    .forEach { _priceSharedFlow.tryEmit(it) }
            }

            else -> {
                logger.warn { "Unknown channel: $channel" }
            }
        }
    }

    private fun reconnect() = scope.launch {
        logger.warn { "Reconnecting WebSocket in 5 seconds..." }
        delay(5_000)
        connect()
    }

    private fun startPingJob() {
        scope.launch {
            while (isRunning) {
                delay(30_000)
                logger.debug { "Sent ping message" }
                webSocket.send("ping")
            }
        }
    }
}