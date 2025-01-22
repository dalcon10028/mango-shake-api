package why_mango.bitget.enums

import com.google.gson.annotations.SerializedName

/**
 * Order expiration date.
 * Required if the orderType is limit
 */
enum class TimeInForce {
    /**
     * Immediate or cancel
     */
    @SerializedName("ioc") IOC,

    /**
     * Fill or kill
     */
    @SerializedName("fok") FOK,

    /**
     * Good till canceled(default value)
     */
    @SerializedName("gtc") GTC,

    /**
     * Post only
     */
    @SerializedName("post_only") POST_ONLY,
    ;
}