package why_mango.ohlcv.repository

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import why_mango.ohlcv.entity.OhlcvDay

interface OhlcvDayRepository : CoroutineCrudRepository<OhlcvDay, Long>