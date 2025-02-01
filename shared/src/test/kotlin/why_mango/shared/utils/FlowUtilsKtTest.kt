package why_mango.shared.utils

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.*
import why_mango.utils.windowed

class FlowUtilsKtTest : StringSpec({
    "should emit sliding windows for a finite flow" {

        // 1부터 5까지의 정수를 방출하는 Flow
        val flow = (1..5).map { it.toBigDecimal() }.asFlow()
        // window size 3으로 슬라이딩 윈도우 생성: [1,2,3], [2,3,4], [3,4,5]
        val result = flow.windowed(3).toList()

        result shouldBe listOf(
            listOf(1, 2, 3).map { it.toBigDecimal() },
            listOf(2, 3, 4).map { it.toBigDecimal() },
            listOf(3, 4, 5).map { it.toBigDecimal() }
        )

    }

    "should not emit any window if flow has fewer elements than window size" {

        // Flow의 요소 개수가 2개인 경우 window size 3이면 아무것도 방출되지 않음
        val flow = (1..2).map { it.toBigDecimal() }.asFlow()
        val result = flow.windowed(3).toList()

        result shouldBe emptyList()

    }

    "should emit one window if flow has exactly window size elements" {

        // Flow의 요소 개수가 window size와 동일하면 단 하나의 윈도우만 방출됨
        val flow = (1..3).map { it.toBigDecimal() }.asFlow()
        val result = flow.windowed(3).toList()

        result shouldBe listOf(listOf(1, 2, 3).map { it.toBigDecimal() })

    }

    "should throw IllegalArgumentException if window size is not positive" {

        val flow = (1..5).map { it.toBigDecimal() }.asFlow()
        val exception = shouldThrow<IllegalArgumentException> {
            flow.windowed(0).toList()
        }
        exception.message shouldBe "size > 0 required but it was 0"
    }

})