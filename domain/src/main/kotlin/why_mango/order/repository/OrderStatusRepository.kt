package why_mango.order.repository

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import why_mango.order.entity.OrderStatus

interface OrderStatusRepository : CoroutineCrudRepository<OrderStatus, Long> {
    suspend fun findByWalletId(walletId: Long): Flow<OrderStatus>
}