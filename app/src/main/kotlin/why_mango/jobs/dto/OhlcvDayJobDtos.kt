package why_mango.jobs.dto

import java.time.LocalDate

data class OhlcvDayJobDtos(
    val symbol: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
) {
    init {
        require(symbol.isNotEmpty()) { "symbol should not be empty" }
        require(startDate.isBefore(endDate) || startDate.isEqual(endDate)) { "startDate should be before endDate" }
    }
}
