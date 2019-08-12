package ua.com.lavi.anychange.provider.stub

import ua.com.lavi.anychange.model.CurrencyPairRate
import ua.com.lavi.anychange.provider.AnyCurrencyProvider
import java.math.BigDecimal

class StaticCurrencyProvider : AnyCurrencyProvider {

    private val pairs = hashMapOf<String, CurrencyPairRate>()

    init {
        pairs["USDTUSD"] = CurrencyPairRate("USDT", "USD", BigDecimal.valueOf(1.005), BigDecimal.valueOf(1.01))
        pairs["EURUSD"] = CurrencyPairRate("EUR", "USD", BigDecimal.valueOf(1.3652), BigDecimal.valueOf(1.3655))
    }

    override fun getRate(pair: String): CurrencyPairRate? {
        return pairs[pair]
    }

    override fun key(): String {
        return "static"
    }

    override fun getRates(): Map<String, CurrencyPairRate> {
        return pairs
    }
}