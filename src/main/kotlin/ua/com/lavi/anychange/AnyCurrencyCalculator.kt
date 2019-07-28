package ua.com.lavi.anychange

import ua.com.lavi.anychange.model.CurrencyPairRate
import java.math.BigDecimal

interface AnyCurrencyCalculator {

    /**
     * Find rate according to calculator implementation
     */

    fun rate(amount: BigDecimal, fromCurrency: String, toCurrency: String): BigDecimal

    /**
     * Get all supported currency rates bid/ask
     */

    fun rates(): List<CurrencyPairRate>

}