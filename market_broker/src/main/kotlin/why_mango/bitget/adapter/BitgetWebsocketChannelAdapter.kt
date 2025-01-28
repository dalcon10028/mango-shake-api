package why_mango.bitget.adapter

import com.google.gson.*
import why_mango.bitget.enums.*
import java.lang.reflect.Type

class BitgetWebsocketChannelAdapter : JsonSerializer<WebsocketChannel>, JsonDeserializer<WebsocketChannel> {
    override fun serialize(src: WebsocketChannel?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src?.value)
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): WebsocketChannel {
        return (TickerChannel.from(json?.asString!!) ?: CandleStickChannel.from(json.asString!!))!!
    }
}