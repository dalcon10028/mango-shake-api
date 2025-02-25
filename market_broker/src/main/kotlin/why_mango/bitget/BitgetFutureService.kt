package why_mango.bitget

import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import java.math.BigDecimal

import why_mango.bitget.dto.market.*
import why_mango.bitget.dto.trade.*
import why_mango.bitget.enums.*
import why_mango.utils.*

@Service
class BitgetFutureService(
    private val bitgetRest: BitgetRest,
) {
    val demoSymbolSet = setOf(SBTCSUSDT, SETHSUSDT, SEOSSUSDT, SXRPSUSDT)

    companion object {
        private const val SBTCSUSDT = "SBTCSUSDT"
        private const val SETHSUSDT = "SETHSUSDT"
        private const val SEOSSUSDT = "SEOSSUSDT"
        private const val SXRPSUSDT = "SXRPSUSDT"
    }

    private fun getProductType(symbol: String) = if (demoSymbolSet.contains(symbol)) ProductType.SUSDT_FUTURES else ProductType.USDT_FUTURES

    private fun getMarginCoin(symbol: String) = if (demoSymbolSet.contains(symbol)) "SUSDT" else "USDT"

    suspend fun getTicker(symbol: String): TickerResponse =
        bitgetRest.getTicker(
            TickerQuery(
                symbol = symbol,
                productType = getProductType(symbol)
            )
        ).data[0]

    suspend fun getCandlestick(symbol: String, granularity: Granularity, limit: Int): List<CandleStickResponse> =
        bitgetRest.getCandlestick(
            CandlestickQuery(
                symbol = symbol,
                productType = getProductType(symbol),
                granularity = granularity.value,
                limit = limit
            )
        ).data
            .map {
                val (timestamp, open, high, low, close, volume, amount) = it
                CandleStickResponse(
                    timestamp = timestamp.toLong(),
                    open = open.toBigDecimal(),
                    high = high.toBigDecimal(),
                    low = low.toBigDecimal(),
                    close = close.toBigDecimal(),
                    volume = volume.toBigDecimal(),
                    amount = amount.toBigDecimal()
                )
            }

    suspend fun getHistoryCandlestick(symbol: String, granularity: Granularity, limit: Int): List<HistoryCandleStickResponse> =
        bitgetRest.getHistoryCandlestick(
            HistoryCandlestickQuery(
                symbol = symbol,
                productType = getProductType(symbol),
                granularity = granularity.value,
                limit = limit
            )
        ).data
            .map {
                val (timeStamp, open, high, low, close, volume, amount) = it
                HistoryCandleStickResponse(
                    timeStamp = timeStamp.toLong(),
                    open = open.toBigDecimal(),
                    high = high.toBigDecimal(),
                    low = low.toBigDecimal(),
                    close = close.toBigDecimal(),
                    volume = volume.toBigDecimal(),
                    amount = amount.toBigDecimal()
                )
            }

    suspend fun openLong(
        symbol: String,
        size: BigDecimal,
        price: BigDecimal? = null,
        orderId: String? = null,
        presetStopSurplusPrice: BigDecimal? = null,
        presetStopLossPrice: BigDecimal? = null,
    ): PlaceOrderResponse {
        val request = PlaceOrderRequest(
            symbol,
            marginCoin = getMarginCoin(symbol),
            size = size,
            price = price,
            productType = getProductType(symbol),
            side = Side.BUY,
            tradeSide = TradeType.OPEN,
            orderType = price?.let { OrderType.LIMIT } ?: OrderType.MARKET,
            clientOid = orderId,
            presetStopSurplusPrice = presetStopSurplusPrice,
            presetStopLossPrice = presetStopLossPrice
        )
        return bitgetRest.placeOrder(request).data
    }

    suspend fun openShort(
        symbol: String,
        size: BigDecimal,
        price: BigDecimal? = null,
        orderId: String? = null,
        presetStopSurplusPrice: BigDecimal?= null,
        presetStopLossPrice: BigDecimal? = null,
    ): PlaceOrderResponse {
        val request = PlaceOrderRequest(
            symbol,
            marginCoin = getMarginCoin(symbol),
            size = size,
            price = price,
            productType = getProductType(symbol),
            side = Side.SELL,
            tradeSide = TradeType.OPEN,
            orderType = price?.let { OrderType.LIMIT } ?: OrderType.MARKET,
            clientOid = orderId,
            presetStopSurplusPrice = presetStopSurplusPrice,
            presetStopLossPrice = presetStopLossPrice
        )
        return bitgetRest.placeOrder(request).data
    }

    suspend fun closeLong(symbol: String) = flashClose(symbol, PositionDirection.LONG)

    suspend fun closeShort(symbol: String) = flashClose(symbol, PositionDirection.SHORT)

    suspend fun flashClose(symbol: String, holdSide: PositionDirection? = null): Boolean =
        bitgetRest.flashClosePosition(
            FlashClosePositionRequest(
                symbol,
                productType = getProductType(symbol),
                holdSide = holdSide
            )
        ).data.failureList.isEmpty()

    suspend fun getContractConfig(symbol: String): ContractConfigResponse =
        bitgetRest.getContractConfig(
            ContractConfigQuery(
                symbol = symbol
            )
        ).data.first { it.symbol == symbol }
}