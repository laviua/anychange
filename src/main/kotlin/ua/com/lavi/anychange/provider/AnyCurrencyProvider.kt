package ua.com.lavi.anychange.provider

import ua.com.lavi.anychange.model.ProviderPairRate

interface AnyCurrencyProvider {

    /**
     * Get bid/ask rates where Key is the symbol pair
     */
    fun getRates(): Map<String, ProviderPairRate>

    /**
     * Get bid/ask rat for specified pair
     */
    fun getRate(pair: String): ProviderPairRate?

    /**
     * Get uniq provider key
     */
    fun key(): String

}