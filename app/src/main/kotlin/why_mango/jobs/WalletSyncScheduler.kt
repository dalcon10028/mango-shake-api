package why_mango.jobs

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.delay
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate
import why_mango.enums.*
import kotlinx.coroutines.flow.*
import org.springframework.context.ApplicationEventPublisher
import why_mango.component.slack.Color
import why_mango.component.slack.Field
import why_mango.component.slack.SlackEvent
import why_mango.component.slack.Topic
import why_mango.wallet.WalletFactory
import why_mango.wallet.WalletService

@Component
class WalletSyncScheduler(
    private val publisher: ApplicationEventPublisher,
    private val walletFactory: WalletFactory,
    private val walletService: WalletService,
) {

    private val logger = KotlinLogging.logger {}

    /**
     * 매일 오전 9(utc0)시에 암호화폐 마감
     */
    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
//    @Scheduled(fixedDelay = 1000 * 60 * 60 * 24)
    suspend fun syncWalletForCryptoCurrency() {
        try {
            val baseDate = LocalDate.now().minusDays(1)
            logger.info { "Start syncWalletForCryptoCurrency: $baseDate" }

            walletService.getWalletsWithoutSecurities(ApiProvider.UPBIT)
//                .chunked(20)
                .mapNotNull { wallet ->
                    delay(100)
                    walletFactory.get(ApiProvider.UPBIT).syncWallet(wallet.id)
                }
                .map { wallet ->
                    walletService.createWalletSecuritiesSnapshot(wallet, baseDate)
                }
                .collect { logger.info { "Sync wallet: $it" } }

        } catch (e: Exception) {
            logger.error { e }
            publisher.publishEvent(
                SlackEvent(
                    topic = Topic.ERROR,
                    title = "Ohlcv day error",
                    color = Color.DANGER,
                    fields = listOf(
                        Field("Error", e.localizedMessage ?: "Unknown error")
                    )
                )
            )
        }
    }
}