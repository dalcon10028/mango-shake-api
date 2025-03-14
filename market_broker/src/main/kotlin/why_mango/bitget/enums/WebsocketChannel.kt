package why_mango.bitget.enums

import com.google.gson.annotations.SerializedName

interface WebsocketChannel {
    val value: String
}

enum class HistoryPositionChannel(
    override val value: String,
): WebsocketChannel {
    /**
     * history_position
     */
    @SerializedName("positions-history")
    HISTORY_POSITION("positions-history"),
    ;

    companion object {
        fun from(value: String): HistoryPositionChannel = entries.first { it.value == value }
    }
}

enum class TickerChannel(
    override val value: String,
) : WebsocketChannel {
    /**
     * ticker
     */
    @SerializedName("ticker")
    TICKER("ticker"),
    ;
}

enum class CandleStickChannel(
    override val value: String,
) : WebsocketChannel {
    /**
     * 1 minute
     */
    @SerializedName("candle1m")
    CANDLE_1MIN("candle1m"),

    /**
     * 5 minutes
     */
    @SerializedName("candle5m")
    CANDLE_5MIN("candle5m"),

    /**
     * 15 minutes
     */
    @SerializedName("candle15m")
    CANDLE_15MIN("candle15m"),

    /**
     * 30 minutes
     */
    @SerializedName("candle30m")
    CANDLE_30MIN("candle30m"),

    /**
     * 1 hour
     */
    @SerializedName("candle1H")
    CANDLE_1HOUR("candle1H"),

    /**
     * 4 hours
     */
    @SerializedName("candle4H")
    CANDLE_4HOUR("candle4H"),

    /**
     * 12 hours
     */
    @SerializedName("candle12H")
    CANDLE_12HOUR("candle12H"),

    /**
     * 1 day
     */
    @SerializedName("candle1D")
    CANDLE_1DAY("candle1D"),

    ;
}
