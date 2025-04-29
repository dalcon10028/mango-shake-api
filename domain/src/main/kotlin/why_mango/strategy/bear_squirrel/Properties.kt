package why_mango.strategy.bear_squirrel

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import java.math.BigDecimal
import java.math.RoundingMode

@ConfigurationProperties(prefix = "strategy.bear-squirrel")
@EnableConfigurationProperties(Properties::class)
data class Properties(
    val entryAmount: BigDecimal,
    val leverage: BigDecimal,
    val timePeriod: String, // ex) 1m, 5m, 15m, 1h, 4h, 1d
    val universe: List<String>, // XRPUSDT, DOGEUSDT
) {
    init {
        entryAmount.setScale(16, RoundingMode.FLOOR)
    }
}
