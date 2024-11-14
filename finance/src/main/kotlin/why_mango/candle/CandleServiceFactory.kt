package why_mango.candle

import org.springframework.stereotype.Component
import why_mango.enums.AssetType

@Component
class CandleServiceFactory(
    private val services: List<CandleService>,
) {
    fun get(market: AssetType): CandleService = services.first { it.market == market }
}