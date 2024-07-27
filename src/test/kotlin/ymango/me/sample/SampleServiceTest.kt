package ymango.me.sample

import io.kotest.common.runBlocking
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class SampleServiceTest(
    private val sampleService: SampleService
) : FunSpec({
    test("sample test") {
        runBlocking {
            coroutineScope {
                sampleService.findRedWines()
            }.let {
                println(it)
            }
        }
    }

})
