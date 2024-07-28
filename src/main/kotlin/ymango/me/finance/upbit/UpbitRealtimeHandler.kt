package ymango.me.finance.upbit

import io.github.oshai.kotlinlogging.KotlinLogging
import okhttp3.*
import okio.ByteString
import org.springframework.stereotype.Component
import java.util.UUID

private val logger = KotlinLogging.logger {}

@Component
class UpbitRealtimeHandler : WebSocketListener() {

    init {
        val client = OkHttpClient()
        Request.Builder().url("wss://api.upbit.com/websocket/v1").build().let {
            client.newWebSocket(it, this)
            client.dispatcher.executorService.shutdown()
        }
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        val ticket = Ticket(UUID.randomUUID().toString(), "ticker", listOf("KRW-SOL"))
        logger.debug { "Sending : $ticket" }
        webSocket.send(ticket.toString())
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        logger.debug { "Receiving : $text" }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        logger.debug { "Receiving bytes : ${bytes.hex()}" }
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        logger.info { "Closing : $code / $reason" }
        webSocket.close(NORMAL_CLOSURE_STATUS, reason)
        webSocket.cancel()
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        logger.error(t) { "Error : ${t.message}" }
    }

    companion object {
        private const val NORMAL_CLOSURE_STATUS = 1000
    }

    private data class Ticket(
        val ticket: String, // 요청자를 식별할 수 있는 값
        val type: String, // ticker(현재가), trade(체결), orderbook(호가)
        val codes: List<String>, // 구독할 마켓의 종목 코드, 대문자
//        val isOnlySnapshot: Boolean? = null, // snapshot만 받을지 여부
//        val isOnlyRealtime: Boolean? = null, // realtime만 받을지 여부
//        val format: String? // DEFAULT(기본형), SIMPLE(축약형)
    ) {
        override fun toString(): String {
            // [{\"ticket\":\"test example\"},{\"type\":\"ticker\",\"codes\":[\"KRW-SOL\"]}]
            return "[{\"ticket\":\"$ticket\"},{\"type\":\"$type\",\"codes\":[\"${codes.joinToString("\",\"")}\"]}]"
        }
    }
}