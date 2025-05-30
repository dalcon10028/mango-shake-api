package why_mango.web_socket

import kotlinx.coroutines.*
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import why_mango.bitget.websocket.*
import why_mango.strategy.bear_squirrel.BearSquirrelTradingMachine
import why_mango.strategy.bollinger_bands_trend.BollingerBandTrendTradingMachine

@Configuration
class WebSocketInitializer(
    private val publicClient: BitgetPublicWebsocketClient,
    private val privateClient: BitgetPrivateWebsocketClient,
//    private val bollingerBandSqueeszeTradingMachine: BollingerBandTrendTradingMachine
    private val bearSquirrelTradingMachine: BearSquirrelTradingMachine,
) {
    @Bean
    fun applicationRunner() = ApplicationRunner {
        runBlocking {
            privateClient.connect()
            publicClient.connect()
            delay(1000)
            bearSquirrelTradingMachine.initialize()
//            bollingerBandSqueeszeTradingMachine.subscribeEventFlow()
//            machine.subscribeEventFlow()
        }
    }
}