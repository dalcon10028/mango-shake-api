package why_mango.bitget.enums

import com.google.gson.annotations.SerializedName

/**
 * Trade side
 */
enum class Side {
    /**
     * Buy(one-way-mode); Long position direction(hedge-mode)
     */
    @SerializedName("buy") BUY,
    /**
     * Sell(one-way-mode); Short position direction(hedge-mode)
     */
    @SerializedName("sell") SELL,
    ;
}