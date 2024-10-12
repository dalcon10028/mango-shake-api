package why_mango.transaction

import kotlinx.coroutines.flow.Flow
import why_mango.enums.ApiProvider
import why_mango.transaction.entity.Transaction

interface TransactionService {
    val apiProvider: ApiProvider

    suspend fun find(walletId: Long): Flow<Transaction>

    suspend fun create(transaction: TransactionCreate): Transaction
}