package why_mango.bitget.enums

import com.google.gson.annotations.SerializedName

/**
 * Position direction
 */
enum class PositionDirection {
    /**
     * long position
     */
    @SerializedName("long") LONG,

    /**
     * short position
     */
    @SerializedName("short") SHORT,

    ;
}