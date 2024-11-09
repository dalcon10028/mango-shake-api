package why_mango.market_broker.impl

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import java.util.*


@Service
class UpbitMarketBrokerService(
    private val upbitRest: UpbitRest,
    private val walletService: WalletService,
    private val tickerSymbolService: TickerSymbolService,
) : MarketBrokerService {
    override val apiProvider = ApiProvider.UPBIT

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getClosedOrderStatus(walletId: Long, symbol: String?): Flow<OrderClosedResponse> {
        val wallet: WalletModel = walletService.getWallet(walletId)

        val markets = if (symbol != null)
            flowOf("KRW-${symbol}")
        else tickerSymbolService.getTickerSymbols()
            .map { tickerSymbol -> "${tickerSymbol.baseCurrency}-${tickerSymbol.symbol}" }

        return markets
            .map { market -> OrderClosedQuery(market) }
            .map { query -> upbitRest.getClosedOrders(generateToken(wallet.appKey, wallet.appSecret, query.serializeToMap()), query) }
            .flatMapMerge { it.asFlow() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getOpenOrderStatus(walletId: Long, symbol: String?): Flow<OrderOpenResponse> {
        val wallet: WalletModel = walletService.getWallet(walletId)

        val markets = if (symbol != null)
            flowOf("KRW-${symbol}")
        else tickerSymbolService.getTickerSymbols()
            .map { tickerSymbol -> "${tickerSymbol.baseCurrency}-${tickerSymbol.symbol}" }

        return markets
            .map { market -> OrderOpenQuery(market) }
            .map { query -> upbitRest.getOpenOrders(generateToken(wallet.appKey, wallet.appSecret, query.serializeToMap()), query) }
            .flatMapMerge { it.asFlow() }
    }

    private fun generateToken(appKey: String, appSecret: String, queryMap: Map<String, Any>): String {
        val queryString = queryMap
            .toList()
            .sortedBy { (key, _) -> key }
            .joinToString("&") { (key, value) -> "$key=$value" }

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