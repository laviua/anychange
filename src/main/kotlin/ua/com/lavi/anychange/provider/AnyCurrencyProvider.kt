package ua.com.lavi.anychange.provider

import ua.com.lavi.anychange.model.CurrencyPairRate

interface AnyCurrencyProvider {

    /**
     * Get bid/ask rates where Key is the symbol pair
     */
    fun getRates(): Map<String, CurrencyPairRate>

    /**
     * Get bid/ask rat for specified pair
     */
    fun getRate(pair: String): CurrencyPairRate?

    /**
     * Get uniq provider key
     */
    fun key(): String

}