package why_mango.wallet.repository

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import why_mango.wallet.entity.WalletSnapshot

interface WalletSnapshotRepository : CoroutineCrudRepository<WalletSnapshot, Long>