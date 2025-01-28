package why_mango.jobs

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import why_mango.strategy.bollinger_band.BollingerBandStrategyService
import java.time.LocalDateTime

@Component
class BollingerBandScheduler(
    private val strategyService: BollingerBandStrategyService,
) {
    private val logger = KotlinLogging.logger {}

    /**
     * 1분 마다 실행
     */
//    @Scheduled(cron = "0 */1 * * * *", zone = "Asia/Seoul")
    suspend fun nextTick() {
        logger.info { "BollingerBandScheduler nextTick" }
        strategyService.next(LocalDateTime.now())
    }

    /**
     * 15분 마다 실행
     */
//    @Scheduled(cron = "0 */15 * * * *", zone = "Asia/Seoul")
//    suspend fun closePosition() {
//        strategyService.close()
//    }
}