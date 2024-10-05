package why_mango.wallet.repository

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import why_mango.wallet.entity.WalletSecurity

interface WalletSecurityRepository : CoroutineCrudRepository<WalletSecurity, Long> {
    suspend fun findByWalletId(walletId: Long): Flow<WalletSecurity>
}