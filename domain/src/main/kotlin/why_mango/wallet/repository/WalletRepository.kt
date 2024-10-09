package why_mango.wallet.repository

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import why_mango.enums.ApiProvider
import why_mango.wallet.entity.Wallet

interface WalletRepository : CoroutineCrudRepository<Wallet, Long> {
    suspend fun existsByApiProviderAndAppKey(apiProvider: ApiProvider, appKey: String): Boolean
    suspend fun findByApiProvider(provider: ApiProvider): Flow<Wallet>
}