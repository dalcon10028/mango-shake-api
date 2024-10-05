package why_mango.wallet

import org.springframework.stereotype.Component
import why_mango.enums.ApiProvider
import why_mango.exception.ErrorCode
import why_mango.exception.MangoShakeException

@Component
class WalletFactory(
    private val walletService: List<WalletService>
) {
    init {
        require(walletService.isNotEmpty()) { "WalletService must not be empty" }
        require(walletService.size == ApiProvider.entries.size)
    }

    fun get(provider: ApiProvider) = walletService.find { it.apiProvider == provider }
        ?: throw MangoShakeException(ErrorCode.RESOURCE_NOT_FOUND, "WalletService not found")
}