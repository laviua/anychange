package ua.com.lavi.anychange

import ua.com.lavi.anychange.model.AnychangeSide
import ua.com.lavi.anychange.model.CurrencyPairRate
import java.math.BigDecimal

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
    fun exchange(symbolPair: String, side: AnychangeSide, amount: BigDecimal): BigDecimal

}