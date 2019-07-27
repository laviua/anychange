package ua.com.lavi.anychange.provider

import ua.com.lavi.anychange.model.CurrencyPairRate

interface AnyCurrencyProvider {

    fun getRates(): Map<String, CurrencyPairRate>

    fun getRate(pair: String): CurrencyPairRate?

    fun key(): String

}