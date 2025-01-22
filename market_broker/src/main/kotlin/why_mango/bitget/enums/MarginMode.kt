package why_mango.bitget.enums

import com.google.gson.annotations.SerializedName

enum class MarginMode {
    @SerializedName("isolated") ISOLATED,
    @SerializedName("crossed") CROSSED,
    ;
}