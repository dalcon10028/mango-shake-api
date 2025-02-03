package why_mango.operation

import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import why_mango.strategy.bollinger_band.StefanoTradingMachine

@RestController
@RequestMapping("/operation")
class StefanoTradingOperation(
    private val stefanoTradingMachine: StefanoTradingMachine,
) {
    @PutMapping("/state/reset")
    suspend fun resetState() {
        stefanoTradingMachine.resetState()
    }
}