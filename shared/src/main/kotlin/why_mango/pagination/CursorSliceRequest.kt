package why_mango.pagination

import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.unsorted

data class CursorSliceRequest(
    val next: Long = 0,
    val limit: Int,
    val sort: Sort = unsorted()
)
