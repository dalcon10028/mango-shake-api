package why_mango.bitget.enums

import com.google.gson.annotations.SerializedName

enum class WebsocketAction {
    @SerializedName("snapshot") SNAPSHOT,
    @SerializedName("update") UPDATE,
}