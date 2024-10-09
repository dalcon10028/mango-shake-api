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
    assert(this.lastSyncedAt != null)
    return WalletSecurityModel(
        id = this.id!!,
        walletId = this.walletId,
        symbol = this.symbol,
        currency = this.currency,
        balance = this.balance,
        locked = this.locked,
        averageBuyPrice = this.averageBuyPrice,
        lastSyncedAt = this.lastSyncedAt!!,
    )
}

fun Wallet.toModel(securities: Map<String, WalletSecurityModel>?): WalletModel {
    assert(this.id != null)
    assert(this.lastSyncedAt != null)
    assert(this.createdAt != null)
    return WalletModel(
        id = this.id!!,
        apiProvider = this.apiProvider,
        status = this.status,
        appKey = this.appKey,
        appSecret = this.appSecret,
        additionalInfo = this.additionalInfo,
        securities = securities,
        memo = this.memo,
        lastSyncedAt = this.lastSyncedAt!!,
        createdAt = this.createdAt!!,
    )
}