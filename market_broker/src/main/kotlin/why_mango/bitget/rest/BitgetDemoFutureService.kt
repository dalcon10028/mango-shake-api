package why_mango.bitget.rest

import org.springframework.stereotype.Service
import why_mango.bitget.BitgetFutureService
import why_mango.bitget.BitgetRest
import java.math.BigDecimal

import why_mango.bitget.dto.market.*
import why_mango.bitget.dto.trade.*
import why_mango.bitget.enums.*
import why_mango.utils.*

@Service
class BitgetDemoFutureService(
    private val bitgetRest: BitgetRest,
) : BitgetFutureService {
    override val productType = ProductType.SUSDT_FUTURES

    companion object {
        private const val MARGIN_COIN = "SUSDT"
    }

    override suspend fun getTicker(symbol: String): TickerResponse =
        bitgetRest.getTicker(
            TickerQuery(
                symbol = symbol,
                productType = productType
            )
        ).data[0]

    override suspend fun getCandlestick(symbol: String, granularity: Granularity, limit: Int): List<CandleStickResponse> =
        bitgetRest.getCandlestick(
            CandlestickQuery(
                symbol = symbol,
                productType = productType,
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

    override suspend fun getHistoryCandlestick(symbol: String, granularity: Granularity, limit: Int): List<HistoryCandleStickResponse> =
        bitgetRest.getHistoryCandlestick(
            HistoryCandlestickQuery(
                symbol = symbol,
                productType = productType,
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

    override suspend fun openLong(
        symbol: String,
        size: BigDecimal,
        price: BigDecimal?,
        orderId: String?,
        presetStopSurplusPrice: BigDecimal?,
        presetStopLossPrice: BigDecimal?,
    ): PlaceOrderResponse {
        val request = PlaceOrderRequest(
            symbol,
            marginCoin = MARGIN_COIN,
            size = size,
            price = price,
            productType = productType,
            side = Side.BUY,
            tradeSide = TradeType.OPEN,
            orderType = price?.let { OrderType.LIMIT } ?: OrderType.MARKET,
            clientOid = orderId,
            presetStopSurplusPrice = presetStopSurplusPrice,
            presetStopLossPrice = presetStopLossPrice
        )
        return bitgetRest.placeOrder(request).data
    }

    override suspend fun openShort(
        symbol: String,
        size: BigDecimal,
        price: BigDecimal?,
        orderId: String?,
        presetStopSurplusPrice: BigDecimal?,
        presetStopLossPrice: BigDecimal?,
    ): PlaceOrderResponse {
        val request = PlaceOrderRequest(
            symbol,
            marginCoin = MARGIN_COIN,
            size = size,
            price = price,
            productType = productType,
            side = Side.SELL,
            tradeSide = TradeType.OPEN,
            orderType = price?.let { OrderType.LIMIT } ?: OrderType.MARKET,
            clientOid = orderId,
            presetStopSurplusPrice = presetStopSurplusPrice,
            presetStopLossPrice = presetStopLossPrice
        )
        return bitgetRest.placeOrder(request).data
    }

    override suspend fun closeLong(symbol: String) = flashClose(symbol, PositionDirection.LONG)

    override suspend fun closeShort(symbol: String) = flashClose(symbol, PositionDirection.SHORT)

    override suspend fun flashClose(symbol: String, holdSide: PositionDirection?): Boolean =
        bitgetRest.flashClosePosition(
            FlashClosePositionRequest(
                symbol,
                productType = productType,
                holdSide = holdSide
            )
        ).data.failureList.isEmpty()

}