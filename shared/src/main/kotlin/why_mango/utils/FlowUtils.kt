package why_mango.utils

import kotlinx.coroutines.flow.*

/**
 * Emit a list of values that are collected in a sliding window.
 * The window is moved by one element at a time.
 */
suspend fun <T> Flow<T>.windowed(size: Int = 9): Flow<List<T>> = flow {
    require(size > 0) { "Window size must be greater than 0" }
    val window = ArrayDeque<T>(size)
    collect { value ->
        if (window.size == size) {
            window.removeFirst()
        }
        window.add(value)
        if (window.size == size) {
            // window의 복사본을 방출 (immutable한 List)
            emit(window.toList())
        }
    }
}