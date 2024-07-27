package ymango.me.finance.upbit

import io.kotest.common.runBlocking
import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UpbitServiceTest(
    private val upbitService: UpbitService
) : FunSpec({

    test("findApiKeys") {
        runBlocking {
            coroutineScope {
                val async1 = async { upbitService.findApiKeys() }
                val async2 = async { upbitService.findApiKeys() }
                val async3 = async { upbitService.findApiKeys() }

                // join
                async1.await()
                async2.await()
                async3.await()
            }

        }
    }
})
