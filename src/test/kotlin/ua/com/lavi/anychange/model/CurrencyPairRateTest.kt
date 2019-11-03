package ua.com.lavi.anychange.model

import org.junit.Assert.assertTrue
import org.junit.Test
import ua.com.lavi.anychange.exception.InvalidFeeException
import java.math.BigDecimal

class CurrencyPairRateTest {

    @Test
    fun should_apply_fee() {
        val rate = CurrencyPairRate("BTC", "USDT", BigDecimal.valueOf(1000), BigDecimal.valueOf(1200))
        val rateWithFee = rate.withFee(BigDecimal.TEN)
        assertTrue(rateWithFee.baseAsset == "BTC")
        assertTrue(rateWithFee.quoteAsset == "USDT")
        assertTrue(rateWithFee.bid.compareTo(BigDecimal.valueOf(900)) == 0)
        assertTrue(rateWithFee.ask.compareTo(BigDecimal.valueOf(1320)) == 0)
    }

    @Test(expected = InvalidFeeException::class)
    fun should_not_apply_fee() {
        val rate = CurrencyPairRate("BTC", "USDT", BigDecimal.valueOf(1000), BigDecimal.valueOf(1200))
        rate.withFee(BigDecimal.TEN.negate())
    }
}