package why_mango.transaction.repository

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import why_mango.transaction.entity.Transaction

interface TransactionRepository : CoroutineCrudRepository<Transaction, Long> {
    suspend fun findByWalletId(walletId: Long): Flow<Transaction>
}