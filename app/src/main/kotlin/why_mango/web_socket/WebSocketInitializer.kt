package why_mango.web_socket

import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import why_mango.bitget.websocket.BitgetPrivateWebsocketClient
import why_mango.bitget.websocket.BitgetPublicDemoWebsocketClient
import why_mango.strategy.bollinger_band.StefanoTradingMachine

@Component
class WebSocketInitializer(
    private val publicClient: BitgetPublicDemoWebsocketClient,
    private val privateClient: BitgetPrivateWebsocketClient,
    private val machine: StefanoTradingMachine,
) {
    @Bean
    suspend fun applicationRunner() = ApplicationRunner {
        publicClient.connect()
//        privateClient.connect()
        machine.subscribeEventFlow()
    }
}