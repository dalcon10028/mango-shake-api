package why_mango.component.slack

import com.slack.api.model.Field

data class Field(
    val title: String,
    val value: String,
    val short: Boolean = false,
) {
    fun toField(): Field = Field.builder()
        .title(title)
        .value(value)
        .valueShortEnough(short)
        .build()
}
