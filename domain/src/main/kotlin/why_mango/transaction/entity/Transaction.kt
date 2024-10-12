package why_mango.transaction.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.*
import why_mango.transaction.enums.TransactionType
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Table("transaction")
class Transaction (
    @Id
    @Column("id")
    val id: Long? = null,

    @Column("wallet_id")
    val walletId: Long,

    @Column("date")
    val date: LocalDate,

    @Column("sequence")
    val sequence: Long,

    @Column("transaction_type")
    val transactionType: TransactionType,

    @Column("description")
    val description: String,

    @Column("symbol")
    val symbol: String? = null,

    @Column("volume")
    val volume: BigDecimal? = null,

    @Column("settlement_amount")
    val settlementAmount: BigDecimal,

    @Column("trade_amount")
    val tradeAmount: BigDecimal,

    @Column("trade_unit_price")
    val tradeUnitPrice: BigDecimal? = null,

    @Column("fee")
    val fee: BigDecimal = BigDecimal.ZERO,

    @Column("tax")
    val tax: BigDecimal = BigDecimal.ZERO,

    @CreatedDate
    @Column("transaction_time")
    val transactionTime: LocalDateTime? = null,

    @Column("identifier")
    val identifier: String,

    @Column("deposit_balance_change")
    val depositBalanceChange: BigDecimal = BigDecimal.ZERO,

    @Column("security_balance_change")
    val securityBalanceChange: BigDecimal = BigDecimal.ZERO,

    @Column("deposit_balance")
    val depositBalance: BigDecimal,

    @Column("security_balance")
    val securityBalance: BigDecimal? = null,

    @Column("wallet_deposit_balance_change")
    val walletDepositBalanceChange: BigDecimal = BigDecimal.ZERO,

    @Column("wallet_security_balance_change")
    val walletSecurityBalanceChange: BigDecimal = BigDecimal.ZERO,

    @Column("wallet_deposit_balance")
    val walletDepositBalance: BigDecimal,

    @Column("wallet_security_balance")
    val walletSecurityBalance: BigDecimal? = null,

    @Column("memo")
    val memo: String? = null,

    @CreatedDate
    @Column("created_at")
    val createdAt: LocalDateTime? = null,
) {
    init {
        require(sequence > 0) { "sequence must be greater than 0" }
        require(settlementAmount > BigDecimal.ZERO) { "settlementAmount must be greater than 0" }
        require(fee > BigDecimal.ZERO) { "feed must be greater than 0" }
        require(tax > BigDecimal.ZERO) { "tax must be greater than 0" }
//        require(tradeUnitPrice > BigDecimal.ZERO) { "tradeUnitPrice must be greater than 0" }
//        require(volume > BigDecimal.ZERO) { "volume must be greater than 0" }
        require(tradeAmount > BigDecimal.ZERO) { "tradeAmount must be greater than 0" }
        require(depositBalance > BigDecimal.ZERO) { "depositBalance must be greater than 0" }
        require(walletDepositBalance > BigDecimal.ZERO) { "walletDepositBalance must be greater than 0" }
    }
}