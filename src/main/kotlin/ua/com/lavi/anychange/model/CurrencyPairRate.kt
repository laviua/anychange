package ua.com.lavi.anychange.model

import java.math.BigDecimal

data class CurrencyPairRate(val baseAsset: String,
                            val quoteAsset: String,
                            val bid: BigDecimal,
                            val ask: BigDecimal,
                            val pair: String = baseAsset + quoteAsset) {

    fun matchesPair(symbolPair: String): Boolean {
        return pair == symbolPair
    }
}