package why_mango.wallet

import kotlinx.coroutines.flow.Flow
import org.springframework.web.bind.annotation.*
import why_mango.wallet.entity.Wallet

@RestController
@RequestMapping("/wallets")
class WalletController(
    private val walletService: WalletService
) {
    @GetMapping
    suspend fun getWallets(): Flow<Wallet> = walletService.getWallets()


    @PostMapping
    suspend fun createWallet(): Wallet {
        return walletService.createWallet()
    }
}