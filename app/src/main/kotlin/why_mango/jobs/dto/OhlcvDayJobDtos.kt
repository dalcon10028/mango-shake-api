package why_mango.jobs.dto

import java.time.LocalDate

data class OhlcvDayJobDtos(
    val startDate: LocalDate,
    val endDate: LocalDate,
) {
    init {
        require(startDate.isBefore(endDate) || startDate.isEqual(endDate)) { "startDate should be before endDate" }
    }
}
