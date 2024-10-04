package why_mango.wallet

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import why_mango.wallet.entity.Wallet
import why_mango.wallet.repository.WalletRepository


@Service
class WalletService(
    private val walletRepository: WalletRepository
) {
    suspend fun getWallets(): Flow<Wallet> = walletRepository.findAll()

    suspend fun createWallet(create: WalletCreate) = walletRepository.save(WalletMapper.toEntity(create))
}