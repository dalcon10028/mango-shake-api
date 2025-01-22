package why_mango.jobs

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate
import why_mango.enums.*
import kotlinx.coroutines.flow.*
import org.springframework.context.ApplicationEventPublisher
import why_mango.component.slack.*
import why_mango.market_broker.MarketBrokerService
import why_mango.market_broker.impl.UpbitMarketBrokerService
import why_mango.order.OrderService
import why_mango.order.model.OrderStatusSave
import why_mango.wallet.WalletFactory
import why_mango.wallet.WalletService
import why_mango.wallet.enums.Status
import java.time.LocalDateTime

@Component
class OrderStatusSyncScheduler(
    private val publisher: ApplicationEventPublisher,
    private val walletService: WalletService,
    private val orderService: OrderService,
    private val marketBrokerService: UpbitMarketBrokerService,
) {

    private val logger = KotlinLogging.logger {}

    /**
     * 매일 오전 9(utc0)시에 암호화폐 마감
     */
    @OptIn(ExperimentalCoroutinesApi::class)
//    @Scheduled(cron = "0 0 1 * * *", zone = "Asia/Seoul")
//    @Scheduled(fixedDelay = 1000 * 60 * 60 * 24)
    suspend fun syncOrderStatus() {
        try {
            val startDate = LocalDate.now().minusDays(300)

            logger.info { "Start sync order status" }

            walletService.getWalletsWithoutSecurities(Status.ACTIVE)
                .onEach { delay(100) }
                .onEach { wallet ->
                    flowOf(
                        marketBrokerService.getClosedOrderStatus(wallet.id, startDate = startDate).map { OrderStatusSave.from(it, wallet.id) },
                        marketBrokerService.getOpenOrderStatus(wallet.id).map { OrderStatusSave.from(it, wallet.id) }
                    )
                        .flattenMerge()
                        .onEach { orderService.saveOrderStatuses(wallet.id, flowOf(it)) }

                }
                .collect { logger.info { "Sync wallet: $it" } }

        } catch (e: Exception) {
            logger.error(e) { "Order status sync error" }

            publisher.publishEvent(
                SlackEvent(
                    topic = Topic.ERROR,
                    title = "Order status sync error",
                    color = Color.DANGER,
                    fields = listOf(
                        Field("Error", e.localizedMessage ?: "Unknown error")
                    )
                )
            )
        }
    }
}