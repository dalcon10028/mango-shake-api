package why_mango.jobs

import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import org.junit.jupiter.api.Assertions.*

class WalletSyncSchedulerTest: FunSpec({
    test("test") {
        // 1~100
        (1..100).asFlow()
            .buffer(10)
            .collect { item ->
                delay(100)
                println(item)
            }
    }
})