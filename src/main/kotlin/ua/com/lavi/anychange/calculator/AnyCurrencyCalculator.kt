package ua.com.lavi.anychange.calculator

import ua.com.lavi.anychange.model.ExchangeSide
import ua.com.lavi.anychange.model.CurrencyPairRate
import ua.com.lavi.anychange.model.ExchangeRequest
import java.math.BigDecimal

/**
 * Rate calculation between different currencies and providers
 */
interface AnyCurrencyCalculator {

    /**
     * Get all supported currency rates bid/ask
     */

    fun rates(): List<CurrencyPairRate>

    /**
     * Get supported currency rate bid/ask for special symbol pair
     */
    fun rate(symbolPair: String): CurrencyPairRate?

    /**
     * Classical exchange operation
     */
    fun exchange(symbolPair: String, side: ExchangeSide, amount: BigDecimal): BigDecimal

    /**
     * Classical exchange operation
     */
    fun exchange(exchangeRequest: ExchangeRequest): BigDecimal

}