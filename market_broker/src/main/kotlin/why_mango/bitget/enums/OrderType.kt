package why_mango.bitget.enums

import com.google.gson.annotations.SerializedName

/**
 * Order type
 */
enum class OrderType {
    /**
     * Limit orders
     */
    @SerializedName("limit") LIMIT,

    /**
     * Market orders
     */
    @SerializedName("market") MARKET,
}