package ua.com.lavi.anychange.model

import java.math.BigDecimal

data class ProviderPairRate(val baseAsset: String,
                            val quoteAsset: String,
                            val bid: BigDecimal,
                            val ask: BigDecimal,
                            val pair: String = baseAsset + quoteAsset) {

    fun matchesPair(lookupPair: String): Boolean {
        return pair == lookupPair
    }
}