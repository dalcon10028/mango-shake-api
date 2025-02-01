package why_mango.utils

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.asFlux
import java.math.BigDecimal

/**
 * Emit a list of values that are collected in a sliding window.
 * The window is moved by one element at a time.
 */
fun Flow<BigDecimal>.windowed(size: Int, step: Int = 1): Flow<List<BigDecimal>> =
    this.asFlux()
        .window(size, step)
        .flatMap { it.collectList() }
        .asFlow()
        .filter { it.size == size }