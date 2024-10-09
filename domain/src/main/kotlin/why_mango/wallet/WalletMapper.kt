package why_mango.wallet

import why_mango.wallet.entity.Wallet
import why_mango.wallet.entity.WalletSecurity



fun WalletCreate.toEntity(): Wallet {
    return Wallet(
        apiProvider = this.apiProvider,
        appKey = this.appKey,
        appSecret = this.appSecret,
        additionalInfo = this.additionalInfo
    )
}

fun WalletSecurity.toModel(): WalletSecurityModel {
    assert(this.id != null)
    assert(this.createdAt != null)
    return WalletSecurityModel(
        id = this.id!!,
        walletId = this.walletId,
        symbol = this.symbol,
        currency = this.currency,
        balance = this.balance,
        locked = this.locked,
        averageBuyPrice = this.averageBuyPrice,
    )
}

fun Wallet.toModel(securities: Map<String, WalletSecurityModel>): WalletModel {
    return WalletModel(
        id = this.id!!,
        apiProvider = this.apiProvider,
        status = this.status,
        appKey = this.appKey,
        appSecret = this.appSecret,
        additionalInfo = this.additionalInfo,
        securities = securities,
        memo = this.memo,
        createdAt = this.createdAt!!
    )
}