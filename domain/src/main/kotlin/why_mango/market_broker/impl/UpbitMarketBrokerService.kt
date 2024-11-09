package why_mango.market_broker.impl

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.springframework.stereotype.Service
import why_mango.enums.ApiProvider
import why_mango.market_broker.MarketBrokerService
import why_mango.order.entity.OrderStatus
import why_mango.ticker_symbol.TickerSymbolService
import why_mango.upbit.UpbitRest
import kotlinx.coroutines.flow.*
import why_mango.upbit.dto.*
import why_mango.wallet.*
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
            .map { query -> upbitRest.getClosedOrders(wallet.generateToken(), query) }
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
            .map { query -> upbitRest.getOpenOrders(wallet.generateToken(), query) }
            .flatMapMerge { it.asFlow() }
    }

    private fun WalletModel.generateToken(): String {
        val algorithm: Algorithm = Algorithm.HMAC256(this.appSecret)
        return JWT.create()
            .withClaim("access_key", this.appKey)
            .withClaim("nonce", UUID.randomUUID().toString())
            .sign(algorithm)
    }
}