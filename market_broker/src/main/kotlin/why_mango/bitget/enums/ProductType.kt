package why_mango.bitget.enums

import com.google.gson.annotations.SerializedName


enum class ProductType(
    private val value: String,
) {
    @SerializedName("USDT-FUTURES") USDT_FUTURES("USDT-FUTURES"), // USDT professional futures
    @SerializedName("COIN-FUTURES") COIN_FUTURES("COIN-FUTURES"), // Mixed futures
    @SerializedName("USDC-FUTURES") USDC_FUTURES("USDC-FUTURES"), // USDC professional futures
    @SerializedName("SUSDT-FUTURES") SUSDT_FUTURES("SUSDT-FUTURES"), // USDT professional futures demo
    @SerializedName("SCOIN-FUTURES") SCOIN_FUTURES("SCOIN-FUTURES"), // Mixed futures demo
    @SerializedName("SUSDC-FUTURES") SUSDC_FUTURES("SUSDC-FUTURES"), // USDC professional futures demo
    ;

    override fun toString(): String {
        return value
    }
}