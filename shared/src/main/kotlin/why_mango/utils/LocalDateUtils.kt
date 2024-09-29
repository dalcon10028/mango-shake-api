package why_mango.utils

import java.time.LocalDate

fun LocalDate.between(start: LocalDate, end: LocalDate): Boolean {
    return this in start..end
}