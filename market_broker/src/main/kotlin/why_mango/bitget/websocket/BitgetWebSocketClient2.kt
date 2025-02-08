package why_mango.bitget.websocket

import okhttp3.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.concurrent.TimeUnit.*
import okio.ByteString
import org.springframework.stereotype.Service
import why_mango.bitget.config.BitgetProperties
import io.github.oshai.kotlinlogging.KotlinLogging

@Service
class BitgetWebSocketClient2(
    private val bitgetProperties: BitgetProperties,
) {
    private val logger = KotlinLogging.logger {}
    private val client = OkHttpClient.Builder()
        .pingInterval(20, SECONDS)
        .build()

    private var webSocket: WebSocket? = null
    private val messageChannel = MutableSharedFlow<String>()
    private var isConnected = false
    private var reconnectAttempts = 0
    private val maxReconnectAttempts = 5

    fun connect() {
        val request = Request.Builder()
            .url(bitgetProperties.websocketPublicUrl)
            .build()

        webSocket = client.newWebSocket(request, webSocketListener)
    }

    fun disconnect() {
        webSocket?.close(1000, "Client disconnecting")
        client.dispatcher.executorService.shutdown()
        isConnected = false
    }

    fun sendMessage(message: String) {
        require(isConnected) { "WebSocket 연결이 되어있지 않습니다" }
        require(message.isNotEmpty()) { "메시지가 비어있습니다" }
        webSocket?.send(message)
    }

    fun receiveMessage(): Flow<String> = messageChannel.asSharedFlow()

    private val webSocketListener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            logger.info { "🔗 연결 주소: ${webSocket.request().url}" }
            isConnected = true
            reconnectAttempts = 0 // 연결 성공하면 재연결 카운트 초기화
            startPingPong() // 핑퐁 메시지 주기적으로 전송
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            logger.info { "📩 받은 메시지: $text" }
            messageChannel.tryEmit(text)
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            logger.info { "📩 받은 바이너리 메시지: ${bytes.utf8()}" }
            messageChannel.tryEmit(bytes.utf8())
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            logger.info { "❌ WebSocket 닫힘: 코드=$code, 이유=$reason" }
            isConnected = false
            reconnect()
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            logger.error { "🔥 WebSocket 오류 발생: ${t.message}" }
            isConnected = false
            reconnect()
        }
    }

    private fun startPingPong() {
        CoroutineScope(Dispatchers.IO).launch {
            while (isConnected) {
                delay(20000) // 20초마다 핑 메시지 전송
                logger.info { "📤 Ping 전송" }
                webSocket?.send("ping")
            }
        }
    }

    private fun reconnect() {
        if (reconnectAttempts >= maxReconnectAttempts) {
            logger.error { "🚫 최대 재연결 시도 횟수 초과 ($maxReconnectAttempts), 종료" }
            return
        }

        reconnectAttempts++
        val delayMillis = (reconnectAttempts * 2000L).coerceAtMost(10000L) // 점진적 증가
        logger.warn { "🔄 $delayMillis ms 후 WebSocket 재연결 시도 ($reconnectAttempts/$maxReconnectAttempts)" }

        CoroutineScope(Dispatchers.IO).launch {
            delay(delayMillis)
            connect()
        }
    }
}