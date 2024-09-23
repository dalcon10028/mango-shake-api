package why_mango.ohlcv

import why_mango.ohlcv.entity.OhlcvDay

class OhlcvDayMapper {
    companion object {
        fun toEntity(ohlcvDayCreate: OhlcvDayCreate): OhlcvDay {
            return OhlcvDay(
                baseDate = ohlcvDayCreate.baseDate,
                exchange = ohlcvDayCreate.exchange,
                symbol = ohlcvDayCreate.symbol,
                open = ohlcvDayCreate.open,
                high = ohlcvDayCreate.high,
                low = ohlcvDayCreate.low,
                close = ohlcvDayCreate.close,
                volume = ohlcvDayCreate.volume,
            )
        }

        fun toModel(ohlcvDay: OhlcvDay): OhlcvDayModel {
            assert(ohlcvDay.id != null)
            assert(ohlcvDay.createdAt != null)
            return OhlcvDayModel(
                id = ohlcvDay.id!!,
                baseDate = ohlcvDay.baseDate,
                exchange = ohlcvDay.exchange,
                symbol = ohlcvDay.symbol,
                open = ohlcvDay.open,
                high = ohlcvDay.high,
                low = ohlcvDay.low,
                close = ohlcvDay.close,
                volume = ohlcvDay.volume,
                createdAt = ohlcvDay.createdAt!!,
            )
        }
    }
}