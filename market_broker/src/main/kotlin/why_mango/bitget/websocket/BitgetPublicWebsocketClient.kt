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
import why_mango.bitget.model.CandleQueue
import why_mango.serialization.gson.NumberStringSerializer
import java.math.BigDecimal

@Component
class BitgetPublicWebsocketClient(
    private val bitgetProperties: BitgetProperties,
) {
    private val logger = KotlinLogging.logger {}
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val request = Request.Builder()
        .url(bitgetProperties.websocketPublicUrl)
        .build()
    private lateinit var client: OkHttpClient
    private lateinit var webSocket: WebSocket
    private val gson: Gson = GsonBuilder()
        .enableComplexMapKeySerialization()
        .registerTypeAdapter(BigDecimal::class.java, NumberStringSerializer)
        .create()
    private var isRunning = false
    private var pingJob: Job? = null
    private val candles1h = CandleQueue(200)
    private val candles1m = CandleQueue(200)
    private val _priceSharedFlow = MutableSharedFlow<TickerPushEvent>(replay = 1)

    val priceEventFlow
        get() = _priceSharedFlow.asSharedFlow()
    val candlestickEventFlow
        get() = candles1m.candleFlow
    val candlestickEventFlow1h
        get() = candles1h.candleFlow


    fun connect() {
        client = OkHttpClient().newBuilder()
            .connectTimeout(30, SECONDS)
            .writeTimeout(30, SECONDS)
            .readTimeout(30, SECONDS)
            .pingInterval(30, SECONDS)
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                logger.info { "🚀 Connected to bitget WebSocket (${bitgetProperties.websocketPublicUrl})" }
                isRunning = true

                // 기존 pingJob이 있다면 취소 후 재시작
                pingJob?.cancel()
                startPingJob()

                // 구독 메시지 전송
                val subscribeMessage = subscribeChannels {
//                    channel(
//                        instType = ProductType.USDT_FUTURES,
//                        channel = CandleStickChannel.CANDLE_1MIN,
//                        instId = "XRPUSDT"
//                    )
//                    channel(
//                        instType = ProductType.USDT_FUTURES,
//                        channel = CandleStickChannel.CANDLE_1HOUR,
//                        instId = "XRPUSDT"
//                    )
//                    channel(
//                        instType = ProductType.USDT_FUTURES,
//                        channel = TickerChannel.TICKER,
//                        instId = "XRPUSDT"
//                    )
//                    channel(
//                        instType = ProductType.SUSDT_FUTURES,
//                        channel = CandleStickChannel.CANDLE_1MIN,
//                        instId = "SXRPSUSDT"
//                    )
                    channel(
                        instType = ProductType.SUSDT_FUTURES,
                        channel = CandleStickChannel.CANDLE_1HOUR,
                        instId = "SXRPSUSDT"
                    )
                    channel(
                        instType = ProductType.SUSDT_FUTURES,
                        channel = TickerChannel.TICKER,
                        instId = "SXRPSUSDT"
                    )
                }
                webSocket.send(gson.toJson(subscribeMessage))
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                if (text == "pong") {
                    logger.debug { "📩 Received pong message" }
                    return
                }
                val baseType = object : TypeToken<BitgetWebsocketResponse<JsonElement>>() {}.type
                val response: BitgetWebsocketResponse<JsonElement> = gson.fromJson(text, baseType)
                response.event?.let {
                    logger.debug { "📩 $it event received ${response.arg}" }
                } ?: handleResponse(response.arg.channel, response.data)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                logger.debug { "Received bytes: $bytes" }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                logger.info { "Closed WebSocket connection with code $code and reason $reason" }
                isRunning = false
                pingJob?.cancel()  // 종료 시에도 pingJob 취소

                reconnect()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                logger.error(t) { "🔥 WebSocket failure" }
                isRunning = false
                pingJob?.cancel()  // 실패 시에도 pingJob 취소

                if (t is EOFException) {
                    logger.warn { "Server closed the WebSocket connection. Attempting to reconnect..." }
                }
                reconnect()
            }
        })

        client.dispatcher.executorService.shutdown()
    }

    private fun handleResponse(channel: String, json: JsonElement?) {
        when (channel) {
            CandleStickChannel.CANDLE_1MIN.value,
            CandleStickChannel.CANDLE_5MIN.value,
            CandleStickChannel.CANDLE_15MIN.value,
            CandleStickChannel.CANDLE_30MIN.value,
            -> {
                val candlestickType = object : TypeToken<List<List<String>>>() {}.type
                gson.fromJson<List<List<String>>>(json, candlestickType)
                    .map { CandleStickPushEvent.from(it) }
                    .forEach { candles1m.add(it) }
            }

            CandleStickChannel.CANDLE_1HOUR.value, -> {
                val candlestickType = object : TypeToken<List<List<String>>>() {}.type
                gson.fromJson<List<List<String>>>(json, candlestickType)
                    .map { CandleStickPushEvent.from(it) }
                    .forEach { candles1h.add(it) }
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
        logger.warn { "🔄 Reconnecting WebSocket in 5 seconds..." }
        delay(5_000)
        connect()
    }

    private fun startPingJob() {
        pingJob = scope.launch {
            while (isRunning) {
                delay(30_000)
                logger.debug { "📤 Sent ping message" }
                webSocket.send("ping")
            }
        }
    }
}