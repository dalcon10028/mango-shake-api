package why_mango.bitget.enums

import com.google.gson.Gson
import feign.RequestTemplate
import feign.codec.Encoder
import java.lang.reflect.Type

class BitgetGsonEncoder(private val gson: Gson) : Encoder {
    override fun encode(p0: Any?, p1: Type?, p2: RequestTemplate?) {
        TODO("Not yet implemented")
    }
}