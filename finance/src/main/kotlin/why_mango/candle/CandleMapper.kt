package why_mango.candle

import why_mango.upbit.dto.CandleDayResponse

fun CandleDayResponse.toModel(): DayCandleModel {
    return DayCandleModel(
        baseDate = candleDateTimeKst.toLocalDate(),
        open = openingPrice,
        high = highPrice,
        low = lowPrice,
        close = tradePrice,
        volume = candleAccTradeVolume,
    )
}