package why_mango.market_broker.impl

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import org.springframework.stereotype.Service
import why_mango.enums.ApiProvider
import why_mango.market_broker.MarketBrokerService
import why_mango.ticker_symbol.TickerSymbolService
import why_mango.upbit.UpbitRest
import why_mango.upbit.dto.*
import why_mango.utils.serializeToMap
import why_mango.wallet.*
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


@Service
class UpbitMarketBrokerService(
    private val upbitRest: UpbitRest,
    private val walletService: WalletService,
    private val tickerSymbolService: TickerSymbolService,
) : MarketBrokerService {
    private val logger = KotlinLogging.logger {}
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'00:00:00+09:00")
    override val apiProvider = ApiProvider.UPBIT

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getClosedOrderStatus(
        walletId: Long,
        symbol: String? = null,
        startDate: LocalDate = LocalDate.now().minusDays(7),
        endDate: LocalDate = LocalDate.now(),
    ): Flow<OrderClosedResponse> {
        val wallet: WalletModel = walletService.getWallet(walletId)

        val markets = if (symbol != null)
            flowOf("KRW-${symbol}")
        else tickerSymbolService.getTickerSymbols()
            .map { tickerSymbol -> "${tickerSymbol.baseCurrency}-${tickerSymbol.symbol}" }
        /*
        /  startDate 부터 endDate 까지의 주문 내역을 가져온다.
        /  startDate + 7일 단위로 요청을 보내야 한다.
        */

        return generateSequence(startDate) { it.plusDays(7) }
            .takeWhile { it.isBefore(endDate) || it.isEqual(endDate) }
            .asFlow()
            // start_time 과 end_time 은 Time Zone 이 포함된 ISO-8601 포맷(ex. 2024-03-13T00:00:00+09:00) 이어야 합니다.
            .map { it.atStartOfDay().format(formatter) }
            .onEach { logger.info { "Start time: $it" } }
            .flatMapMerge { startTime -> markets.map { market -> OrderClosedQuery(market, start_time = startTime) } }
            .onEach { delay(100) }
            .map { query -> upbitRest.getClosedOrders(generateToken(wallet.appKey, wallet.appSecret, query.serializeToMap()), query) }
            .flatMapMerge { it.asFlow() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getOpenOrderStatus(walletId: Long, symbol: String? = null): Flow<OrderOpenResponse> {
        val wallet: WalletModel = walletService.getWallet(walletId)

        val markets = if (symbol != null)
            flowOf("KRW-${symbol}")
        else tickerSymbolService.getTickerSymbols()
            .map { tickerSymbol -> "${tickerSymbol.baseCurrency}-${tickerSymbol.symbol}" }

        return markets
            .onEach { delay(100) }
            .map { market -> OrderOpenQuery(market) }
            .map { query -> upbitRest.getOpenOrders(generateToken(wallet.appKey, wallet.appSecret, query.serializeToMap()), query) }
            .flatMapMerge { it.asFlow() }
    }

    private fun generateToken(appKey: String, appSecret: String, queryMap: Map<String, Any>): String {
        val queryString = queryMap.entries.joinToString("&") { p -> "${p.key}=${p.value}" }

        val algorithm: Algorithm = Algorithm.HMAC256(appSecret)
        val md = MessageDigest.getInstance("SHA-512").also {
            it.update(queryString.toByteArray(StandardCharsets.UTF_8))
        }

        val queryHash = String.format("%0128x", BigInteger(1, md.digest()))

        return JWT.create()
            .withClaim("access_key", appKey)
            .withClaim("nonce", UUID.randomUUID().toString())
            .withClaim("query_hash", queryHash)
            .withClaim("query_hash_alg", "SHA512")
            .sign(algorithm)
    }
}