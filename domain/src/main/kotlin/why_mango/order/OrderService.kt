package why_mango.order

import kotlinx.coroutines.flow.*
import org.springframework.stereotype.Service
import why_mango.order.entity.OrderStatus
import why_mango.order.model.OrderStatusSave
import why_mango.order.repository.OrderStatusRepository

@Service
class OrderService(
    private val orderStatusRepository: OrderStatusRepository,
) {
    suspend fun getOrderStatus(walletId: Long) = orderStatusRepository.findByWalletId(walletId)

    suspend fun saveOrderStatuses(walletId: Long, orderStatuses: Flow<OrderStatusSave>) {
        val orderStatusUuidSet: Set<String> = orderStatusRepository.findByWalletId(walletId)
            .map { it.uuid }
            .toSet()

        orderStatuses
            .filter { it.uuid !in orderStatusUuidSet }
            .map { it.toEntity() }
            .let { orderStatusRepository.saveAll(it).collect { } }
    }

}