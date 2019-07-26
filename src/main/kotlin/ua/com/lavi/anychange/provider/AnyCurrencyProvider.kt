package ua.com.lavi.anychange.provider

import ua.com.lavi.anychange.model.PairRate

interface AnyCurrencyProvider {

    fun getRates(): Map<String, PairRate>

    fun key(): String

    fun getPair(pair: String): PairRate?

}