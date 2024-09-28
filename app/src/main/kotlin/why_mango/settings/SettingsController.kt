package why_mango.settings

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/settings")
class SettingsController {

    @GetMapping
    suspend fun getSettings(): Flow<Map<String, Any>> = flowOf(
        mapOf(
            "settings" to mapOf(
                "title" to "Why Mango",
                "description" to "A simple app to demonstrate the use of Spring WebFlux"
            )
        )
    )
}