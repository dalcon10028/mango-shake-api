package why_mango.web_socket

import com.slack.api.bolt.App
import com.slack.api.bolt.socket_mode.SocketModeApp
import com.slack.api.model.event.MessageBotEvent
import com.slack.api.model.event.MessageEvent
import kotlinx.coroutines.*
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import why_mango.strategy.machines.BollingerBandSqueeszeTradingMachine
import why_mango.strategy.machines.StefanoTradingMachine

@Configuration
class SlackTradingHelperBot(
    private val machine: BollingerBandSqueeszeTradingMachine,
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @Bean
    fun slackAppRunner() = ApplicationRunner {
        runBlocking {
            val app = App().apply {
                command("/state") { req, ctx ->
                    ctx.respond { it.text("StefanoTradingMachine status: `${machine.state}`") }
                    ctx.ack()
                }

                command("/close-all") { req, ctx ->
                    scope.launch { machine.closeAll() }
                    ctx.respond { it.text("모든 포지션을 청산합니다.") }
                    ctx.ack()
                }

                event(MessageEvent::class.java) { req, ctx ->
                    val text = "You said ${req.event.text} at <#${req.event.channel}>"
                    val res = ctx.say { it.text(text) }
//                    ctx.logger.debug("say result - {}", res)
                    ctx.ack()
                }

                event(MessageBotEvent::class.java) { req, ctx ->
                    val text = "You said ${req.event.text} at <#${req.event.channel}>"
                    val res = ctx.say { it.text(text) }
//                    ctx.logger.debug("say result - {}", res)
                    ctx.ack()
                }
            }

            SocketModeApp(app).startAsync()
        }
    }
}