package why_mango.stat

import org.springframework.stereotype.Service
import why_mango.stat.repository.AumDayRepository
import java.time.LocalDate

@Service
class StatService(
    private val aumDayRepository: AumDayRepository,
) {

    suspend fun aum(startDate: LocalDate, endDate: LocalDate) = aumDayRepository.findWalletSnapshotAum(startDate, endDate)
}