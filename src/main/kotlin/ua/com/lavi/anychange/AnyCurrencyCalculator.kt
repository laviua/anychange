package ua.com.lavi.anychange

import java.math.BigDecimal

interface AnyCurrencyCalculator {

    /**
     * Find rate according to calculator implementation
     */

    fun rate(amount: BigDecimal, fromCurrency: String, toCurrency: String): BigDecimal

}