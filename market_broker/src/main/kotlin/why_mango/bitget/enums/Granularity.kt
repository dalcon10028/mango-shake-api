package why_mango.bitget.enums

enum class Granularity(
    val value: String
) {
    ONE_MINUTE("1m"), // 1 minute
    THREE_MINUTES("3m"), // 3 minutes
    FIVE_MINUTES("5m"), // 5 minutes
    FIFTEEN_MINUTES("15m"), // 15 minutes
    THIRTY_MINUTES("30m"), // 30 minutes
    ONE_HOUR("1H"), // 1 hour
    FOUR_HOURS("4H"), // 4 hours
    SIX_HOURS("6H"), // 6 hours
    TWELVE_HOURS("12H"), // 12 hours
    ONE_DAY("1D"), // 1 day
    THREE_DAYS("3D"), // 3 days
    ONE_WEEK("1W"), // 1 week
    ONE_MONTH("1M"), // monthly line
    SIX_HOURS_UTC("6Hutc"), // UTC 6 hour line
    TWELVE_HOURS_UTC("12Hutc"), // UTC 12 hour line
    ONE_DAY_UTC("1Dutc"), // UTC 1-day line
    THREE_DAYS_UTC("3Dutc"), // UTC 3-day line
    ONE_WEEK_UTC("1Wutc"), // UTC weekly line
    ONE_MONTH_UTC("1Mutc"), // UTC monthly line
    ;

    companion object {
        fun from(value: String): Granularity {
            return entries.first { it.value == value }
        }
    }
}