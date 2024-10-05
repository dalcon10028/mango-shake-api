package why_mango.candle

import org.springframework.stereotype.Component
import why_mango.enums.Market

@Component
class CandleServiceFactory(
    private val services: List<CandleService>,
) {
    fun get(market: Market): CandleService = services.first { it.market == market }
}