package why_mango.bitget.enums

import com.google.gson.annotations.SerializedName

enum class Granularity(
    val value: String,
) {

    @SerializedName("1m")
    ONE_MINUTE("1m"), // 1 minute

    @SerializedName("3m")
    THREE_MINUTES("3m"), // 3 minutes

    @SerializedName("5m")
    FIVE_MINUTES("5m"), // 5 minutes

    @SerializedName("15m")
    FIFTEEN_MINUTES("15m"), // 15 minutes

    @SerializedName("30m")
    THIRTY_MINUTES("30m"), // 30 minutes

    @SerializedName("1H")
    ONE_HOUR("1H"), // 1 hour

    @SerializedName("4H")
    FOUR_HOURS("4H"), // 4 hours

    @SerializedName("6H")
    SIX_HOURS("6H"), // 6 hours

    @SerializedName("12H")
    TWELVE_HOURS("12H"), // 12 hours

    @SerializedName("1D")
    ONE_DAY("1D"), // 1 day

    @SerializedName("3D")
    THREE_DAYS("3D"), // 3 days

    @SerializedName("1W")
    ONE_WEEK("1W"), // 1 week

    @SerializedName("1M")
    ONE_MONTH("1M"), // monthly line

    @SerializedName("6Hutc")
    SIX_HOURS_UTC("6Hutc"), // UTC 6 hour line

    @SerializedName("12Hutc")
    TWELVE_HOURS_UTC("12Hutc"), // UTC 12 hour line

    @SerializedName("1Dutc")
    ONE_DAY_UTC("1Dutc"), // UTC 1-day line

    @SerializedName("3Dutc")
    THREE_DAYS_UTC("3Dutc"), // UTC 3-day line

    @SerializedName("1Wutc")
    ONE_WEEK_UTC("1Wutc"), // UTC weekly line

    @SerializedName("1Mutc")
    ONE_MONTH_UTC("1Mutc"), // UTC monthly line
    ;

    companion object {
        fun from(value: String): Granularity {
            return entries.first { it.value == value }
        }
    }
}