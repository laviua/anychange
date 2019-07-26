package ua.com.lavi.anychange.provider

import ua.com.lavi.anychange.model.PairRate

interface CurrencyProvider {

    fun getRates(): Map<String, PairRate>

    fun providerKey(): String

    fun getPair(pair: String): PairRate?

}