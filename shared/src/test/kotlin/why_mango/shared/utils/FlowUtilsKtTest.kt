package why_mango.shared.utils

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.*
import why_mango.utils.groupBy
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


    "groupBy should group elements by key" {

        val flow = flowOf("apple", "apricot", "banana", "blueberry", "avocado")
        val grouped = flow.groupBy { it.first() }.toList()

        grouped.size shouldBe 2  // 'a', 'b' 두 개의 그룹이 나와야 함
        val groupedMap = grouped.toMap()

        groupedMap.keys shouldBe setOf('a', 'b')

        groupedMap['a']!!.toList() shouldBe listOf("apple", "apricot", "avocado")
        groupedMap['b']!!.toList() shouldBe listOf("banana", "blueberry")
    }

    "groupBy should return empty flow when input is empty" {

        val flow = emptyFlow<String>()
        val grouped = flow.groupBy { it.length }.toList()

        grouped.shouldBeEmpty()
    }

    "groupBy should handle duplicate keys" {

        val flow = flowOf(1, 2, 3, 4, 5, 6)
        val grouped = flow.groupBy { it % 2 }.toList()

        grouped.size shouldBe 2 // Even, Odd 두 그룹이 나와야 함
        val groupedMap = grouped.toMap()

        groupedMap[0]!!.toList() shouldBe listOf(2, 4, 6) // 짝수 그룹
        groupedMap[1]!!.toList() shouldBe listOf(1, 3, 5) // 홀수 그룹
    }

    "groupBy should work with a single element" {

        val flow = flowOf("single")
        val grouped = flow.groupBy { it.length }.toList()

        grouped.size shouldBe 1
        val groupedMap = grouped.toMap()

        groupedMap[6]!!.toList() shouldBe listOf("single")
    }

    "groupBy should handle all elements having the same key" {

        val flow = flowOf("one", "two", "three", "four")
        val grouped = flow.groupBy { "constantKey" }.toList()

        grouped.size shouldBe 1
        val groupedMap = grouped.toMap()

        groupedMap["constantKey"]!!.toList() shouldBe listOf("one", "two", "three", "four")
    }

    "groupBy should handle all elements having different keys" {

        val flow = flowOf("a", "bb", "ccc", "dddd")
        val grouped = flow.groupBy { it.length }.toList()

        grouped.size shouldBe 4
        val groupedMap = grouped.toMap()

        groupedMap[1]!!.toList() shouldBe listOf("a")
        groupedMap[2]!!.toList() shouldBe listOf("bb")
        groupedMap[3]!!.toList() shouldBe listOf("ccc")
        groupedMap[4]!!.toList() shouldBe listOf("dddd")
    }

    "groupBy should handle rapid incoming values correctly" {

        val flow = (1..100).asFlow()
        val grouped = flow.groupBy { it % 10 }.toList()

        grouped.size shouldBe 10 // 0 ~ 9 까지 총 10개의 그룹이 나와야 함
        val groupedMap = grouped.toMap()

        for (i in 0..9) {
            groupedMap[i]!!.toList().size shouldBe 10 // 각 그룹에 10개씩 있어야 함
        }
    }
})