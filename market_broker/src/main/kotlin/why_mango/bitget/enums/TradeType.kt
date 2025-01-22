package why_mango.bitget.enums

import com.google.gson.annotations.SerializedName

/**
 * Trade type
 * Only required in hedge-mode
 */
enum class TradeType {

    /**
     * Open position
     */
    @SerializedName("open") OPEN,

    /**
     * Close position
     */
    @SerializedName("close") CLOSE,
    ;

}