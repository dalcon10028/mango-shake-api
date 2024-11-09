package why_mango.order

import org.springframework.stereotype.Service
import why_mango.order.repository.OrderStatusRepository

@Service
class OrderService(
    private val orderStatusRepository: OrderStatusRepository,
) {
    suspend fun getOrderStatus(walletId: Long) = orderStatusRepository.findByWalletId(walletId)
}