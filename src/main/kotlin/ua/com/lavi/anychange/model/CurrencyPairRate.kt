package ua.com.lavi.anychange.model

import java.math.BigDecimal

data class CurrencyPairRate(val baseAsset: String,
                            val quoteAsset: String,
                            val bid: BigDecimal,
                            val ask: BigDecimal) {

    fun pair(): String {
        return baseAsset + quoteAsset
    }

    fun reversedPair(): String {
        return quoteAsset + baseAsset
    }
}