package why_mango.web_socket

import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import why_mango.bitget.websocket.BitgetDemoWebSocketClient
import why_mango.strategy.bollinger_band.BollingerBandStateMachine

@Component
class WebSocketInitializer(
    private val client: BitgetDemoWebSocketClient,
    private val machine: BollingerBandStateMachine,
) {
    @Bean
    fun applicationRunner() = ApplicationRunner {
        client.connect()
        machine.subscribeEventFlow()
    }
}