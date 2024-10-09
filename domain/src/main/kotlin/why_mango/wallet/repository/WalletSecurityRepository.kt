package why_mango.wallet.repository

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import why_mango.wallet.entity.WalletSecurity

interface WalletSecurityRepository : CoroutineCrudRepository<WalletSecurity, Long> {
    suspend fun findByWalletId(walletId: Long): Flow<WalletSecurity>

    suspend fun findByWalletIdIn(walletIds: List<Long>): Flow<WalletSecurity>

    // https://docs.spring.io/spring-data/relational/reference/r2dbc/query-methods.html#r2dbc.repositories.modifying
    suspend fun deleteByWalletIdAndSymbol(walletId: Long, symbol: String): Int
}