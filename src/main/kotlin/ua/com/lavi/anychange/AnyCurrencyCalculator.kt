package ua.com.lavi.anychange

import ua.com.lavi.anychange.model.AnychangeResult
import ua.com.lavi.anychange.model.AnychangeSide
import ua.com.lavi.anychange.model.CurrencyPairRate
import java.math.BigDecimal

interface AnyCurrencyCalculator {

    /**
     * Find rate according to calculator implementation by bid side
     */

    fun calculate(amount: BigDecimal, fromCurrency: String, toCurrency: String): AnychangeResult

    /**
     * Get all supported currency rates bid/ask
     */

    fun rates(): List<CurrencyPairRate>

    /**
     * Classical exchange operation
     */
    fun exchange(symbolPair: String, side: AnychangeSide, amount: BigDecimal): AnychangeResult

}