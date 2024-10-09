package why_mango.wallet.repository

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import why_mango.enums.ApiProvider
import why_mango.wallet.entity.Wallet
import why_mango.wallet.enums.Status

interface WalletRepository : CoroutineCrudRepository<Wallet, Long> {
    suspend fun existsByApiProviderAndAppKeyAndStatus(apiProvider: ApiProvider, appKey: String, status: Status = Status.ACTIVE): Boolean
    suspend fun findByApiProviderAndStatus(provider: ApiProvider, status: Status = Status.ACTIVE): Flow<Wallet>
}