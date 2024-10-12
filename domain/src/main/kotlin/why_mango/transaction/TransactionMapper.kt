package why_mango.transaction

import why_mango.transaction.*
import why_mango.transaction.entity.Transaction

fun TransactionCreate.toEntity(): Transaction {
    return Transaction(
        walletId = walletId,
        date = date,
        sequence = sequence,
        transactionType = transactionType,
        description = description,
        symbol = symbol,
        volume = volume,
        settlementAmount = settlementAmount,
        tradeAmount = tradeAmount,
        tradeUnitPrice = tradeUnitPrice,
        fee = fee,
        tax = tax,
        transactionTime = transactionTime,
        identifier = identifier,
        depositBalanceChange = depositBalanceChange,
        securityBalanceChange = securityBalanceChange,
        depositBalance = depositBalance,
        securityBalance = securityBalance,
        walletDepositBalanceChange = walletDepositBalanceChange,
        walletSecurityBalanceChange = walletSecurityBalanceChange,
        walletDepositBalance = walletDepositBalance,
        walletSecurityBalance = walletSecurityBalance,
    )
}