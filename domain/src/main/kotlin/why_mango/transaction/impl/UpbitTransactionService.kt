package why_mango.transaction.impl

import org.springframework.stereotype.Service
import why_mango.enums.*
import why_mango.transaction.repository.*
import kotlinx.coroutines.flow.*
import why_mango.transaction.entity.*
import why_mango.transaction.*

@Service
class UpbitTransactionService(
    private val transactionRepository: TransactionRepository
) : TransactionService {

    override val apiProvider: ApiProvider = ApiProvider.UPBIT

    override suspend fun find(walletId: Long): Flow<Transaction> =
        transactionRepository.findByWalletId(walletId)

    override suspend fun create(transaction: TransactionCreate): Transaction =
        transactionRepository.save(transaction.toEntity())
}