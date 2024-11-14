package why_mango.stat.repository

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import why_mango.stat.entity.AumDay
import why_mango.stat.projection.AumProjection
import java.time.LocalDate

interface AumDayRepository : CoroutineCrudRepository<AumDay, Long> {
    suspend fun findByBaseDateBetween(startDate: LocalDate, endDate: LocalDate): Flow<AumDay>

    @Query("""
        SELECT 
            a.base_date,
            SUM(
                (a.balance + a.locked) * CASE WHEN a.symbol = 'KRW' THEN 1 ELSE b.close END
            ) AS asset_valuation
        FROM wallet_security_snapshot a LEFT JOIN ohlcv_day b
            ON a.symbol = b.symbol AND a.base_date = b.base_date
        WHERE a.base_date BETWEEN :startDate AND :endDate 
        GROUP BY a.wallet_id, a.base_date
        ORDER BY a.wallet_id, a.base_date DESC 
    """)
    suspend fun findWalletSnapshotAum(startDate: LocalDate, endDate: LocalDate): Flow<AumProjection>
}