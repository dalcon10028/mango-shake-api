package why_mango.wallet

import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.web.bind.annotation.*
import why_mango.wallet.dto.WalletResponse
import why_mango.wallet.dto.toResponse

@Tag(name = "Wallet", description = "Wallet API")
@RestController
@RequestMapping("/wallets")
class WalletController(
    private val walletService: WalletService,
    private val walletFactory: WalletFactory,
) {
    @GetMapping
    suspend fun getWallets(): Flow<WalletResponse> = walletService.getWallets().map { it.toResponse() }

    @GetMapping("/{walletId}")
    suspend fun getWallet(
        @Parameter(description = "Wallet ID", example = "1")
        @PathVariable walletId: Long
    ): WalletResponse = walletService.getWallet(walletId).toResponse()

    @PostMapping
    suspend fun createWallet(
        @RequestBody walletCreate: WalletCreate
    ): WalletResponse = walletFactory.get(walletCreate.apiProvider).createWallet(walletCreate).toResponse()
}