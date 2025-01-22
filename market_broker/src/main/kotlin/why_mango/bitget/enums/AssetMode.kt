package why_mango.bitget.enums

import com.google.gson.annotations.SerializedName

enum class AssetMode {

    /**
     * single : single asset mode
     */
    @SerializedName("single") SINGLE,

    /**
     * union multi-Assets mode
     */
    @SerializedName("union") UNION,

    ;
}