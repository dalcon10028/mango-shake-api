package why_mango.ohlcv.repository

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import why_mango.ohlcv.entity.DayOhlcv

interface DayOhlcvRepository : CoroutineCrudRepository<DayOhlcv, Long>