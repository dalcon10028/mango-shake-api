package why_mango.wallet


import kotlinx.serialization.encodeToString

import why_mango.wallet.entity.Wallet

class WalletMapper {
    companion object {
        fun toEntity(walletCreate: WalletCreate): Wallet {
            return Wallet(
                apiProvider = walletCreate.apiProvider,
                appKey = walletCreate.appKey,
                appSecret = walletCreate.appSecret,
                additionalInfo = walletCreate.additionalInfo
            )
        }

        fun toModel(wallet: Wallet): WalletModel {
            return WalletModel(
                id = wallet.id!!,
                apiProvider = wallet.apiProvider,
                appKey = wallet.appKey,
                appSecret = wallet.appSecret,
                additionalInfo = wallet.additionalInfo,
                createdAt = wallet.createdAt!!
            )
        }
    }
}