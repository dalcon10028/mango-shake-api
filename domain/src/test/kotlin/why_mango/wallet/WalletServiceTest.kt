package why_mango.ohlcv

import io.kotest.core.spec.style.FunSpec
import org.junit.jupiter.api.Assertions.*
import org.springframework.boot.test.context.SpringBootTest
import why_mango.enums.Exchange
import why_mango.wallet.WalletService
import java.math.BigDecimal
import java.time.LocalDate

@SpringBootTest
class WalletServiceTest(
    private val walletService: WalletService
) : FunSpec({
    test("read wallets") {
        walletService.getWallets()
    }
})