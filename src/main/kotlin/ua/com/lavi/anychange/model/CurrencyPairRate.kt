package ua.com.lavi.anychange.model

import ua.com.lavi.anychange.percentOf
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
        return applyFee(fee, fee)
    }

    fun applyFee(bidFee: BigDecimal, askFee: BigDecimal): CurrencyPairRate {
        val bid = bid.minus(bidFee.percentOf(bid)).setScale(scale, roundingMode).stripTrailingZeros()
        val ask = ask.plus(askFee.percentOf(ask)).setScale(scale, roundingMode).stripTrailingZeros()
        return CurrencyPairRate(baseAsset, quoteAsset, bid, ask, scale, roundingMode)
    }
}