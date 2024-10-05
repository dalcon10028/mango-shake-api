package why_mango.candle

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Service
import why_mango.enums.Currency
import why_mango.enums.Market
import why_mango.upbit.UpbitRest
import why_mango.upbit.dto.CandleDayQuary
import why_mango.utils.between
import java.time.LocalDate

import java.util.*

@Service
class CryptoCurrencyCandleService(
    private val upbitRest: UpbitRest,
    private val upbitProperties: UpbitProperties,
) : CandleService {
    override val market: Market = Market.CRYPTO_CURRENCY

    override suspend fun getDayCandles(symbol: String, startDate: LocalDate, endDate: LocalDate): Flow<DayCandleModel> {
        val query = CandleDayQuary(market = "${Currency.KRW}-${symbol}")
        return upbitRest.getCandleDay(generateToken(), query)
            .asFlow().map { it.toModel() }.filter { it.baseDate.between(startDate, endDate) }
    }

    private suspend fun generateToken(): String {
        val algorithm: Algorithm = Algorithm.HMAC256(upbitProperties.secretKey)
        return JWT.create()
            .withClaim("access_key", upbitProperties.accessKey)
            .withClaim("nonce", UUID.randomUUID().toString())
            .sign(algorithm)
    }

    @ConfigurationProperties(prefix = "upbit")
    data class UpbitProperties(
        val accessKey: String,
        val secretKey: String,
    )
}