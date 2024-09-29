package why_mango.settings

import kotlinx.coroutines.flow.Flow
import org.springframework.web.bind.annotation.*
import why_mango.ohlcv.OhlcvDayService
import why_mango.ohlcv.entity.OhlcvDay
import why_mango.upbit.UpbitRest
import why_mango.upbit.dto.ApiKeyResponse


@RestController
@RequestMapping("/settings")
class SettingsController(
    val ohlcvDayService: OhlcvDayService,
    val upbitRest: UpbitRest,
) {

    @GetMapping
    suspend fun getSettings(): Flow<OhlcvDay> = ohlcvDayService.findAll()

    @GetMapping("/test")
    suspend fun getSettingsTest(): List<ApiKeyResponse> = upbitRest.getApiKeys()
}