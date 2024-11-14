package why_mango.stat

import org.springframework.data.repository.query.Param
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/stats")
class StatController(
    private val statService: StatService,
) {
    @GetMapping("/aum")
    suspend fun aum(
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate,
    ) = statService.aum(startDate, endDate)
}