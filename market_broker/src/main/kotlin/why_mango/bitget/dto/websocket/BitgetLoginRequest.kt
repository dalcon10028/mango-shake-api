package why_mango.bitget.dto.websocket

data class BitgetLoginRequest(
    val op: String = "login",
    val args: List<LoginArgs>
)

data class LoginArgs(
    val apiKey: String,
    val passphrase: String,
    val timestamp: Long,
    val sign: String
)
