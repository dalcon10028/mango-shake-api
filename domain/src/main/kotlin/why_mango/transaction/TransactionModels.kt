package why_mango.transaction

import why_mango.transaction.enums.TransactionType
import java.math.BigDecimal
import java.time.*

data class TransactionCreate(
    val walletId: Long,
    val date: LocalDate,
    val sequence: Long,
    val transactionType: TransactionType,
    val description: String,
    val symbol: String?,
    val volume: BigDecimal?,
    val settlementAmount: BigDecimal,
    val tradeAmount: BigDecimal,
    val tradeUnitPrice: BigDecimal?,
    val fee: BigDecimal,
    val tax: BigDecimal,
    val transactionTime: LocalDateTime? = null,
    val identifier: String,
    val depositBalanceChange: BigDecimal,
    val securityBalanceChange: BigDecimal,
    val depositBalance: BigDecimal,
    val securityBalance: BigDecimal?,
    val walletDepositBalanceChange: BigDecimal,
    val walletSecurityBalanceChange: BigDecimal,
    val walletDepositBalance: BigDecimal,
    val walletSecurityBalance: BigDecimal?,
)
