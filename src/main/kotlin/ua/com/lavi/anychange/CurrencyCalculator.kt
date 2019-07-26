package ua.com.lavi.anychange

import java.math.BigDecimal

interface CurrencyCalculator {

    /**
     * Find rate according to calculator implementation
     */

    fun rate(amount: BigDecimal, fromCurrency: String, toCurrency: String): BigDecimal

}