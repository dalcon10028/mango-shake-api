package why_mango.ohlcv

import org.springframework.stereotype.Service
import why_mango.ohlcv.repository.OhlcvDayRepository

@Service
class OhlcvDayService(
    private val ohlcvDayRepository: OhlcvDayRepository,
) {
    suspend fun createOhlcvDay(ohlcvDayCreate: OhlcvDayCreate): OhlcvDayModel =
        OhlcvDayMapper.toEntity(ohlcvDayCreate)
            .let { ohlcvDayRepository.save(it) }
            .let { OhlcvDayMapper.toModel(it) }
}