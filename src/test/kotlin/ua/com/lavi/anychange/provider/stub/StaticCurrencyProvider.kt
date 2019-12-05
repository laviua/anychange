package ua.com.lavi.anychange.provider.stub

import ua.com.lavi.anychange.model.ProviderPairRate
import ua.com.lavi.anychange.provider.AnyCurrencyProvider
import java.math.BigDecimal

class StaticCurrencyProvider : AnyCurrencyProvider {

    private val pairs = hashMapOf<String, ProviderPairRate>()

    init {
        pairs["USDTUSD"] = ProviderPairRate("USDT", "USD", BigDecimal.valueOf(1.005), BigDecimal.valueOf(1.01))
        pairs["EURUSD"] = ProviderPairRate("EUR", "USD", BigDecimal.valueOf(1.3652), BigDecimal.valueOf(1.3655))
    }

    override fun getRate(pair: String): ProviderPairRate? {
        return pairs[pair]
    }

    override fun key(): String {
        return "static"
    }

    override fun getRates(): Map<String, ProviderPairRate> {
        return pairs
    }
}