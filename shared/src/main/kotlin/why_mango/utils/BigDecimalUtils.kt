package why_mango.utils

import java.math.BigDecimal

/**
 * Calculates the take profit price.
 *
 * @param percent The take profit percent as a decimal (e.g. 0.1 means 10%).
 * @param leverage The leverage to apply (e.g. 10 for 10x leverage).
 * @return The take profit price.
 *
 * @sample
 * ```
 * val price = 100.toBigDecimal()
 * val takeProfitPrice = price.takeProfit(0.1, 10)
 * println(takeProfitPrice) // 101.0
 * ```
 */
fun BigDecimal.takeProfitLong(percent: Double, leverage: Int = 1): BigDecimal =
    this * (BigDecimal.ONE + BigDecimal.valueOf(percent / leverage))

/**
 * Calculates the stop loss price.
 *
 * @param percent The stop loss percent as a decimal (e.g. 0.1 means 10%).
 * @param leverage The leverage to apply (e.g. 10 for 10x leverage).
 * @return The stop loss price.
 *
 * @sample
 * ```
 * val price = 100.toBigDecimal()
 * val stopLossPrice = price.stopLoss(0.1, 10)
 * println(stopLossPrice) // 99.0
 * ```
 */
fun BigDecimal.stopLossLong(percent: Double, leverage: Int = 1): BigDecimal =
    this * (BigDecimal.ONE - BigDecimal.valueOf(percent / leverage))

fun BigDecimal.takeProfitShort(percent: Double, leverage: Int = 1): BigDecimal =
    stopLossLong(percent, leverage)

fun BigDecimal.stopLossShort(percent: Double, leverage: Int = 1): BigDecimal =
    takeProfitLong(percent, leverage)