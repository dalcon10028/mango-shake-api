package why_mango.pagination

import kotlinx.coroutines.flow.Flow

data class CursorSlice<T>(
    val items: Flow<T>,
    val next: Long? = null,
)