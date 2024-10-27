package why_mango.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DateSerializer: KSerializer<LocalDate> {
    private val formatter = DateTimeFormatter.ISO_DATE_TIME

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.format(formatter))
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString(), formatter)
    }
}