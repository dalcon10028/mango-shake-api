package why_mango.wallet.repository

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import why_mango.wallet.entity.Wallet

interface WalletRepository : CoroutineCrudRepository<Wallet, Long>