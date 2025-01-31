package why_mango.bitget

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import okhttp3.*
import okio.EOFException
import why_mango.bitget.dto.*
import why_mango.bitget.dto.websocket.BitgetLoginRequest
import why_mango.bitget.dto.websocket.BitgetSubscribeRequest
import why_mango.serialization.gson.NumberStringSerializer
import java.math.BigDecimal
import java.util.concurrent.TimeUnit.SECONDS

abstract class AbstractBitgetWebsocketClient(
    private val baseUrl: String,
) {
    protected val logger = KotlinLogging.logger {}
    private val client = OkHttpClient().newBuilder()
        .pingInterval(10, SECONDS)
        .build()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var webSocket: WebSocket
    protected val gson: Gson = GsonBuilder()
        .enableComplexMapKeySerialization()
        .registerTypeAdapter(BigDecimal::class.java, NumberStringSerializer)
        .create()
    protected var isRunning = false


    fun connect() {
        val request = Request.Builder()
            .url(baseUrl)
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                logger.info { "Connected to Bitget WebSocket ($baseUrl)" }
                isRunning = true
                startPingJob()

                val loginRequest: BitgetLoginRequest? = login()
                if (loginRequest != null) {
                    webSocket.send(gson.toJson(loginRequest))
                } else {
                    webSocket.send(gson.toJson(subscriptionMessage()))
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                logger.debug { "Received message: $text" }

                if (text == "pong") {
                    logger.debug { "Received pong message" }
                    return
                }

                val baseType = object : TypeToken<BitgetWebsocketResponse<JsonElement>>() {}.type
                val response: BitgetWebsocketResponse<JsonElement> = gson.fromJson(text, baseType)

                when (response.event) {
                    "login" -> {
                        logger.info { "Login response received" }
                        webSocket.send(gson.toJson(subscriptionMessage()))
                    }

                    "subscribe" -> {
                        logger.info { "Subscription response received: ${response.arg}" }
                    }

                    "unsubscribe" -> {
                        logger.info { "Unsubscription response received: ${response.arg}" }
                    }

                    "error" -> {
                        logger.error { "Error response received: [${response.code}]: ${response.msg}" }
                    }

                    null -> handleMessage(response.arg.channel, response.data)
                }
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

    /**
     * Websocket will be forcibly disconnected every 24 hours, please add the reconnection mechanism in your code
     */
    private fun reconnect() = scope.launch {
        logger.warn { "Reconnecting WebSocket in 5 seconds..." }
        delay(5_000)
        connect()
    }

    /**
     * Users set a 30 seconds timer to a send string "ping", and expect a string "pong" as response. If no string "pong" received, please reconnect
     */
    private fun startPingJob() {
        scope.launch {
            while (isRunning) {
                delay(30_000)
                logger.debug { "Sent ping message" }
                webSocket.send("ping")
            }
        }
    }

    protected fun<T> parseJson(json: JsonElement?): T = gson.fromJson(json, object : TypeToken<T>() {}.type)

    protected open fun login(): BitgetLoginRequest? = null

    protected abstract fun subscriptionMessage(): BitgetSubscribeRequest

    protected abstract fun handleMessage(channel: String, json: JsonElement?)
}