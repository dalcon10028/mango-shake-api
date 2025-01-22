package why_mango.bitget.enums

import com.google.gson.annotations.SerializedName

/**
 * STP Mode(Self Trade Prevention)
 */
enum class StpMode {
    /**
     * not setting STP(default value)
     */
    @SerializedName("none") NONE,

    /**
     * cancel taker order
     */
    @SerializedName("cancel_taker") CANCEL_TAKER,

    /**
     * cancel maker order
     */
    @SerializedName("cancel_maker") CANCEL_MAKER,

    /**
     * cancel both of taker and maker orders
     */
    @SerializedName("cancel_both") CANCEL_BOTH,

    ;
}