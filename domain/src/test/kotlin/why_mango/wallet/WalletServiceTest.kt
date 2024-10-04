package why_mango.ohlcv

import io.kotest.core.spec.style.FunSpec
import org.springframework.boot.test.context.SpringBootTest
import why_mango.wallet.WalletService

@SpringBootTest
class WalletServiceTest(
    private val walletService: WalletService
) : FunSpec({
    test("read wallets") {
        walletService.getWallets()
    }
})