package why_mango.bitget

import feign.*
import why_mango.bitget.dto.BitgetResponse
import why_mango.bitget.dto.history_candle_stick.HistoryCandlestickQuery

interface BitgetRest {

    /**
     * Get Historical Candlestick
     */
    @RequestLine("GET /api/v2/mix/market/history-candles")
    suspend fun getHistoryCandlestick(@QueryMap query: HistoryCandlestickQuery): BitgetResponse<List<List<String>>>
}