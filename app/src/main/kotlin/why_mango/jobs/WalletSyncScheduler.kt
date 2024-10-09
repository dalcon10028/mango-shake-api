package why_mango.jobs

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import why_mango.candle.CandleServiceFactory
import why_mango.jobs.dto.OhlcvDayJobDtos
import why_mango.ohlcv.OhlcvDayCreate
import why_mango.ohlcv.OhlcvDayService
import java.time.LocalDate
import why_mango.enums.*
import kotlinx.coroutines.flow.*
import why_mango.ticker_symbol.TickerSymbolService
import why_mango.wallet.WalletService

@Component
class WalletSyncScheduler(
    private val walletService: WalletService,
) {

    private val logger = KotlinLogging.logger {}

    /**
     * 매일 오전 9시에 암호화폐 마감
     */
    @Scheduled(cron = "0 0 9 * * *")
//    @Scheduled(fixedDelay = 1000 * 60 * 60 * 24)
    suspend fun syncWalletForCryptoCurrency() {
        try {
            logger.info { "Start syncWalletForCryptoCurrency" }

            walletService.getWallets(ApiProvider.UPBIT)
                .map { wallet -> walletService.syncWallet(wallet.id) }
                .collect { logger.info { "Sync wallet: $it" } }

        } catch (e: Exception) {
            logger.error { e }
        }
    }
}