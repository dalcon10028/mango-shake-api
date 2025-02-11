package why_mango.web_socket

import kotlinx.coroutines.*
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import why_mango.bitget.websocket.*
import why_mango.strategy.machines.StefanoTradingMachine

@Configuration
class WebSocketInitializer(
    private val publicClient: BitgetPublicWebsocketClient,
    private val privateClient: BitgetPrivateWebsocketClient,
    private val machine: StefanoTradingMachine,
//    private val bitgetWebSocketClient2: BitgetWebSocketClient2
) {
    @Bean
    fun applicationRunner() = ApplicationRunner {
        runBlocking {
            privateClient.connect()
            publicClient.connect()
            machine.subscribeEventFlow()
        }
    }
}