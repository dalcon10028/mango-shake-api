package why_mango.wallet.impl

import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.transaction.annotation.Transactional
import why_mango.exception.ErrorCode
import why_mango.exception.MangoShakeException
import why_mango.upbit.UpbitRest
import why_mango.upbit.dto.AccountResponse
import why_mango.wallet.WalletService
import java.time.LocalDateTime
import kotlin.collections.List

import why_mango.enums.*
import kotlinx.coroutines.flow.*
import why_mango.wallet.repository.*
import why_mango.wallet.entity.*
import why_mango.wallet.*
import java.util.*

@Service
class UpbitWalletService(
    private val walletRepository: WalletRepository,
    private val walletSecurityRepository: WalletSecurityRepository,
    walletSecuritySnapshotRepository: WalletSecuritySnapshotRepository,
    private val upbitRest: UpbitRest,
) : WalletService(walletRepository, walletSecurityRepository, walletSecuritySnapshotRepository) {

    override val apiProvider: ApiProvider = ApiProvider.UPBIT

    @Transactional
    override suspend fun createWallet(create: WalletCreate): WalletModel {
        if (walletRepository.existsByApiProviderAndAppKeyAndStatus(create.apiProvider, create.appKey)) {
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

    @Transactional
    override suspend fun syncWallet(walletId: Long): WalletModel {
        val lastSyncedAt = LocalDateTime.now()

        val wallet = walletRepository.findById(walletId) ?: throw MangoShakeException(ErrorCode.RESOURCE_NOT_FOUND, "Wallet not found")
        val securities = walletSecurityRepository.findByWalletId(walletId).toList()
        val assets: List<AccountResponse> = upbitRest.getAccounts(generateToken(wallet.appKey, wallet.appSecret))

        walletRepository.save(wallet.also {
            it.lastSyncedAt = lastSyncedAt
        })

        securities.forEach { security ->
            val asset = assets.find { it.currency == security.symbol }
            if (asset == null) {
                walletSecurityRepository.deleteByWalletIdAndSymbol(walletId, security.symbol)
            } else {
                walletSecurityRepository.save(
                    security.also {
                        it.balance = asset.balance
                        it.locked = asset.locked
                        it.averageBuyPrice = asset.avgBuyPrice
                        it.lastSyncedAt = lastSyncedAt
                    }
                )
            }
        }

        assets.filter { asset -> securities.none { it.symbol == asset.currency } }
            .forEach { asset ->
                walletSecurityRepository.save(
                    WalletSecurity(
                        walletId = walletId,
                        currency = asset.unitCurrency,
                        symbol = asset.currency,
                        balance = asset.balance,
                        locked = asset.locked,
                        averageBuyPrice = asset.avgBuyPrice,
                        lastSyncedAt = lastSyncedAt,
                        createdAt = lastSyncedAt
                    )
                )
            }

        return wallet.toModel(walletSecurityRepository.findByWalletId(walletId).map { it.toModel() }.toList().associateBy { it.symbol })
    }

    private suspend fun generateToken(appKey: String, appSecret: String): String {
        val algorithm: Algorithm = Algorithm.HMAC256(appSecret)
        return JWT.create()
            .withClaim("access_key", appKey)
            .withClaim("nonce", UUID.randomUUID().toString())
            .sign(algorithm)
    }
}