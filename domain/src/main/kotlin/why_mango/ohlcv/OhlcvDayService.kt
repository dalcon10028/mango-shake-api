package why_mango.ohlcv

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import why_mango.ohlcv.entity.OhlcvDay
import why_mango.ohlcv.repository.OhlcvDayRepository

@Service
class OhlcvDayService(
    private val ohlcvDayRepository: OhlcvDayRepository,
) {
    suspend fun createOhlcvDay(ohlcvDayCreate: OhlcvDayCreate): OhlcvDayModel =
        ohlcvDayRepository.findByBaseDateAndSymbolAndCurrency(
            baseDate = ohlcvDayCreate.baseDate,
            symbol = ohlcvDayCreate.symbol,
            currency = ohlcvDayCreate.currency
        )?.let(OhlcvDayMapper::toModel)
            ?: ohlcvDayRepository.save(OhlcvDayMapper.toEntity(ohlcvDayCreate))
                .let(OhlcvDayMapper::toModel)

    suspend fun findAll(): Flow<OhlcvDay> = ohlcvDayRepository.findAll()
}