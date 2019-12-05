package ua.com.lavi.anychange.model

import ua.com.lavi.anychange.exception.InvalidFeeException
import java.math.BigDecimal
import java.math.RoundingMode

data class CurrencyPairRate(val baseAsset: String,
                            val quoteAsset: String,
                            val bid: BigDecimal,
                            val ask: BigDecimal,
                            val scale: Int,
                            val roundingMode: RoundingMode,
                            val pair: String = baseAsset + quoteAsset) {

    fun matchesPair(symbolPair: String): Boolean {
        return pair == symbolPair
    }

    fun applyFee(fee: BigDecimal): CurrencyPairRate {
        if (fee.compareTo(BigDecimal.ZERO) == 0 || fee.compareTo(BigDecimal.ZERO) == -1) {
            throw InvalidFeeException()
        }
        val bid = bid.minus(fee.multiply(bid).divide(BigDecimal.valueOf(100))).setScale(scale, roundingMode).stripTrailingZeros()
        val ask = ask.plus(fee.multiply(ask).divide(BigDecimal.valueOf(100))).setScale(scale, roundingMode).stripTrailingZeros()
        return CurrencyPairRate(baseAsset, quoteAsset, bid, ask, scale, roundingMode)
    }
}