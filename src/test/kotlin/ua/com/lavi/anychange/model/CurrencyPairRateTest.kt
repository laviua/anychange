package ua.com.lavi.anychange.model

import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.math.RoundingMode

class CurrencyPairRateTest {

    @Test
    fun should_apply_fee() {
        val rate = CurrencyPairRate("BTC", "USDT", BigDecimal.valueOf(1000), BigDecimal.valueOf(1200), 8, RoundingMode.HALF_EVEN)
        val rateWithFee = rate.applyFee(BigDecimal.TEN)
        assertTrue(rateWithFee.baseAsset == "BTC")
        assertTrue(rateWithFee.quoteAsset == "USDT")
        assertTrue(rateWithFee.bid.compareTo(BigDecimal.valueOf(900)) == 0)
        assertTrue(rateWithFee.ask.compareTo(BigDecimal.valueOf(1320)) == 0)
    }

    @Test
    fun should_apply_negative_fee() {
        val rate = CurrencyPairRate("BTC", "USDT", BigDecimal.valueOf(1000), BigDecimal.valueOf(1200), 8, RoundingMode.HALF_EVEN)
        val rateWithFee = rate.applyFee(BigDecimal.valueOf(-10))
        assertTrue(rateWithFee.bid.compareTo(BigDecimal.valueOf(1100)) == 0)
        assertTrue(rateWithFee.ask.compareTo(BigDecimal.valueOf(1080)) == 0)
    }

    @Test
    fun should_apply_separate_fee() {
        val rate = CurrencyPairRate("BTC", "USDT", BigDecimal.valueOf(1000), BigDecimal.valueOf(1200), 8, RoundingMode.HALF_EVEN)
        val rateWithFee = rate.applyFee(BigDecimal.valueOf(10), BigDecimal.ZERO)
        assertTrue(rateWithFee.bid.compareTo(BigDecimal.valueOf(900)) == 0)
        assertTrue(rateWithFee.ask.compareTo(BigDecimal.valueOf(1200)) == 0)
    }
}