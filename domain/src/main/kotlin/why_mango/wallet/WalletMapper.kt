package why_mango.wallet

import why_mango.wallet.entity.*
import java.time.LocalDate

fun WalletCreate.toEntity(): Wallet {
    return Wallet(
        apiProvider = this.apiProvider,
        appKey = this.appKey,
        appSecret = this.appSecret,
        additionalInfo = this.additionalInfo,
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
        beginningAssets = this.beginningAssets,
        endingAssets = this.endingAssets,
        depositsDuringPeriod = this.depositsDuringPeriod,
        withdrawalsDuringPeriod = this.withdrawalsDuringPeriod,
        lastSyncedAt = this.lastSyncedAt!!,
        createdAt = this.createdAt!!,
    )
}

fun WalletModel.toSnapshot(baseDate: LocalDate): WalletSnapshot {
    return WalletSnapshot(
        walletId = this.id,
        baseDate = baseDate,
        beginningAssets = this.beginningAssets,
        endingAssets = this.endingAssets,
        depositsDuringPeriod = this.depositsDuringPeriod,
        withdrawalsDuringPeriod = this.withdrawalsDuringPeriod,
    )
}

fun WalletSecurityModel.toSnapshot(walletSnapshotId: Long, baseDate: LocalDate): WalletSecuritySnapshot {
    return WalletSecuritySnapshot(
        walletId = this.walletId,
        walletSnapshotId = walletSnapshotId,
        walletSecurityId = this.id,
        baseDate = baseDate,
        currency = this.currency,
        symbol = this.symbol,
        balance = this.balance,
        locked = this.locked,
        averageBuyPrice = this.averageBuyPrice,
    )
}