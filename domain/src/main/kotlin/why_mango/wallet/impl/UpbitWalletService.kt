package why_mango.wallet.impl

import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import kotlinx.coroutines.flow.toList
import org.springframework.transaction.annotation.Transactional
import why_mango.enums.*
import why_mango.enums.Currency
import why_mango.exception.ErrorCode
import why_mango.exception.MangoShakeException
import why_mango.upbit.UpbitRest
import why_mango.upbit.dto.AccountResponse
import why_mango.wallet.WalletService
import why_mango.wallet.repository.WalletRepository
import why_mango.wallet.*
import why_mango.wallet.entity.Wallet
import why_mango.wallet.entity.WalletSecurity
import why_mango.wallet.repository.WalletSecurityRepository
import java.util.*
import kotlin.collections.List

@Service
class UpbitWalletService(
    private val walletRepository: WalletRepository,
    private val walletSecurityRepository: WalletSecurityRepository,
    private val upbitRest: UpbitRest,
) : WalletService(walletRepository, walletSecurityRepository) {

    override val apiProvider: ApiProvider = ApiProvider.UPBIT

    @Transactional
    override suspend fun createWallet(create: WalletCreate): WalletModel {
        if (walletRepository.existsByApiProviderAndAppKey(create.apiProvider, create.appKey)) {
            throw MangoShakeException(ErrorCode.DUPLICATED_RESOURCE, "Wallet already exists")
        }

        val wallet: Wallet = walletRepository.save(create.toEntity())
        val accounts: List<AccountResponse> = upbitRest.getAccounts(generateToken(create.appKey, create.appSecret))
        val securities = accounts.map {
            WalletSecurity(
                walletId = wallet.id!!,
                currency = it.unitCurrency,
                symbol = it.currency,
                balance = it.balance,
                locked = it.locked,
                averageBuyPrice = it.avgBuyPrice,
            )
        }
        return wallet.toModel(walletSecurityRepository.saveAll(securities).map { it.toModel() }.toList().associateBy { it.symbol })
    }

    private suspend fun generateToken(appKey: String, appSecret: String): String {
        val algorithm: Algorithm = Algorithm.HMAC256(appSecret)
        return JWT.create()
            .withClaim("access_key", appKey)
            .withClaim("nonce", UUID.randomUUID().toString())
            .sign(algorithm)
    }
}