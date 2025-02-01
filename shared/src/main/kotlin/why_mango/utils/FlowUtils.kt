package why_mango.utils

import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.asFlux

/**
 * Emit a list of values that are collected in a sliding window.
 * The window is moved by one element at a time.
 */
fun <T : Any>Flow<T>.windowed(size: Int, step: Int = 1): Flow<List<T>> =
    this.asFlux()
        .window(size, step)
        .flatMap { it.collectList() }
        .asFlow()
        .filter { it.size == size }

/**
 * Groups elements in a Flow<T> by a given key and returns a Flow of (Key, Flow<T>) pairs.
 *
 * This function utilizes Reactor's `groupBy()` internally for efficient real-time grouping,
 * ensuring that elements with the same key are emitted together as a grouped Flow.
 */
fun <T : Any, K> Flow<T>.groupBy(keyMapper: (T) -> K): Flow<Pair<K, Flow<T>>> =
    this.asFlux()
        .groupBy(keyMapper)
        .asFlow()
        .map { it.key() to it.asFlow() }
