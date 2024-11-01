package why_mango.common.pagination

import org.springframework.data.domain.*
import java.util.function.Function

//class A : PageImpl<>

class CursorSliceImp<T>: CursorSlice<T> {
    override fun iterator(): MutableIterator<T> {
        TODO("Not yet implemented")
    }

    override fun getNumber(): Int {
        TODO("Not yet implemented")
    }

    override fun getSize(): Int {
        TODO("Not yet implemented")
    }

    override fun getNumberOfElements(): Int {
        TODO("Not yet implemented")
    }

    override fun getContent(): MutableList<T> {
        TODO("Not yet implemented")
    }

    override fun hasContent(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getSort(): Sort {
        TODO("Not yet implemented")
    }

    override fun isFirst(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isLast(): Boolean {
        TODO("Not yet implemented")
    }

    override fun hasNext(): Boolean {
        TODO("Not yet implemented")
    }

    override fun hasPrevious(): Boolean {
        TODO("Not yet implemented")
    }

    override fun nextPageable(): Pageable {
        TODO("Not yet implemented")
    }

    override fun previousPageable(): Pageable {
        TODO("Not yet implemented")
    }

    override fun <U : Any?> map(converter: Function<in T, out U>): Slice<U> {
        TODO("Not yet implemented")
    }

}