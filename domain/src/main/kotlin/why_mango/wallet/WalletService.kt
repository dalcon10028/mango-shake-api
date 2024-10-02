package why_mango.wallet

import org.springframework.stereotype.Service
import why_mango.enums.ApiProvider
import why_mango.wallet.entity.UpbitAdditionalInfo
import why_mango.wallet.entity.Wallet
import why_mango.wallet.repository.WalletRepository

@Service
class WalletService(
    private val walletRepository: WalletRepository
) {
    suspend fun getWallets() = walletRepository.findAll()

    suspend fun createWallet() = walletRepository.save(
        Wallet(
            apiProvider = ApiProvider.UPBIT,
            appKey = "appKey",
            appSecret = "appSecret",
            additionalInfo = UpbitAdditionalInfo(ApiProvider.UPBIT)
        )
    )
}