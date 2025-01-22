package why_mango.bitget.enums

import com.google.gson.annotations.SerializedName

/**
 * Position mode
 */
enum class PositionMode {
    /**
     * positions in one-way mode
     */
    @SerializedName("one_way_mode") ONE_WAY_MODE,

    /**
     * positions in hedge-mode
     */
    @SerializedName("hedge_mode") HEDGE_MODE,

    ;
}