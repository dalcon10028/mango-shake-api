package why_mango.component.slack

import com.slack.api.model.Field

data class Field(
    val title: String,
    val value: Any,
    val short: Boolean = false,
) {
    fun toField(): Field = Field.builder()
        .title(title)
        .value(value.toString())
        .valueShortEnough(short)
        .build()
}
