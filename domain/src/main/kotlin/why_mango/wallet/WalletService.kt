package why_mango.wallet

import kotlinx.coroutines.flow.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import why_mango.enums.ApiProvider
import why_mango.exception.ErrorCode
import why_mango.exception.MangoShakeException
import why_mango.wallet.repository.*

@Service
abstract class WalletService(
    private val walletRepository: WalletRepository,
    private val walletSecurityRepository: WalletSecurityRepository,
    private val walletSecuritySnapshotRepository: WalletSecuritySnapshotRepository,
) {
    abstract val apiProvider: ApiProvider

    @Transactional(readOnly = true)
    suspend fun getWallets(): Flow<WalletModel> =
        walletRepository.findAll().map { wallet ->
            val securities = walletSecurityRepository.findByWalletId(wallet.id!!)
            wallet.toModel(securities.map { it.toModel() }.toList().associateBy { it.symbol })
        }

    @Transactional(readOnly = true)
    suspend fun getWalletsWithoutSecurities(provider: ApiProvider): Flow<WalletModel> =
        walletRepository.findByApiProviderAndStatus(provider).map { wallet -> wallet.toModel(emptyMap()) }

    @Transactional(readOnly = true)
    suspend fun getWallets(provider: ApiProvider): Flow<WalletModel> {
        val wallets = walletRepository.findByApiProviderAndStatus(provider).toList()

        val mapValues = walletSecurityRepository.findByWalletIdIn(wallets.map { it.id!! })
            .toList()
            .groupBy { it.walletId }
            .mapValues { it.value.map { it.toModel() } }

        return wallets.asFlow().map { wallet ->
            wallet.toModel(mapValues[wallet.id!!]?.associateBy { it.symbol } ?: emptyMap())
        }
    }

    @Transactional(readOnly = true)
    suspend fun getWallet(walletId: Long): WalletModel {
        val wallet = walletRepository.findById(walletId) ?: throw MangoShakeException(ErrorCode.RESOURCE_NOT_FOUND, "Wallet not found")
        val securities = walletSecurityRepository.findByWalletId(walletId)
        return wallet.toModel(securities.map { it.toModel() }.toList().associateBy { it.symbol })
    }

    suspend fun createWalletSecuritiesSnapshot(walletSecurities: List<WalletSecurityModel>) =
        walletSecuritySnapshotRepository.saveAll(walletSecurities.map { it.toSnapshot() })

    abstract suspend fun createWallet(create: WalletCreate): WalletModel

    abstract suspend fun syncWallet(walletId: Long): WalletModel
}