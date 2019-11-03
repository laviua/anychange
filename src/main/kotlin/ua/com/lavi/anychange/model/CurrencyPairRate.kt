package ua.com.lavi.anychange.model

import ua.com.lavi.anychange.exception.InvalidFeeException
import java.math.BigDecimal

data class CurrencyPairRate(val baseAsset: String,
                            val quoteAsset: String,
                            val bid: BigDecimal,
                            val ask: BigDecimal,
                            val pair: String = baseAsset + quoteAsset) {

    fun matchesPair(symbolPair: String): Boolean {
        return pair == symbolPair
    }

    fun withFee(fee: BigDecimal): CurrencyPairRate {
        if (fee.compareTo(BigDecimal.ZERO) == 0 || fee.compareTo(BigDecimal.ZERO) == -1) {
            throw InvalidFeeException()
        }
        val bid = bid.minus(fee.multiply(bid).divide(BigDecimal.valueOf(100)))
        val ask = ask.plus(fee.multiply(ask).divide(BigDecimal.valueOf(100)))
        return CurrencyPairRate(baseAsset, quoteAsset, bid, ask)
    }
}