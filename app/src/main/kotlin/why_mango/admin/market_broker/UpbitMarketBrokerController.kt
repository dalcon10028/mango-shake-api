package why_mango.admin.market_broker

import org.springframework.web.bind.annotation.*
import why_mango.market_broker.impl.UpbitMarketBrokerService

@RestController
@RequestMapping("/admin/market_brokers/upbit")
class UpbitMarketBrokerController(
    private val upbitMarketBrokerService: UpbitMarketBrokerService
) {

    @GetMapping("/orders/open")
    suspend fun openOrder(
        @RequestParam walletId: Long,
        @RequestParam market: String? = null,
    ) = upbitMarketBrokerService.getOpenOrderStatus(walletId, market)

    @GetMapping("/orders/closed")
    suspend fun closedOrder(
        @RequestParam walletId: Long,
        @RequestParam market: String? = null,
    ) = upbitMarketBrokerService.getClosedOrderStatus(walletId, market)

}