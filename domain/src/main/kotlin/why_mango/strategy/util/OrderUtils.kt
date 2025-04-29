package why_mango.strategy.util

import why_mango.bitget.dto.market.ContractConfigResponse
import java.math.BigDecimal
import java.math.RoundingMode

suspend fun orderSize(contractConfig: ContractConfigResponse, entryAmount: BigDecimal, leverage: BigDecimal, price: BigDecimal): BigDecimal {
    val sizeMultiplier = contractConfig.sizeMultiplier            // 0.01

    // 자본 = entryAmount * leverage
    val capital = entryAmount.multiply(leverage)

    // 원시 계산: 자본 / 가격 (충분한 소수점 자리로 계산)
    val rawSize = capital.divide(price, price.scale() + 8, RoundingMode.DOWN)

    // sizeMultiplier 의 정수 배수만큼 뽑아내기
    val multiplierCount = rawSize.divideToIntegralValue(sizeMultiplier)

    // 최종 수량: multiplierCount * sizeMultiplier, 소수점 자리(sizeMultiplier.scale()) 유지
    return multiplierCount
        .multiply(sizeMultiplier)
        .setScale(sizeMultiplier.stripTrailingZeros().scale(), RoundingMode.DOWN)
}