package stub

import ua.com.lavi.anychange.model.PairRate
import ua.com.lavi.anychange.provider.CurrencyProvider
import java.math.BigDecimal

class USDTUSDCurrencyProvider : CurrencyProvider {

    private val pairs = hashMapOf<String, PairRate>()

    init {
        pairs["USDTUSD"] = PairRate("USDT", "USD", BigDecimal.valueOf(1.005), BigDecimal.valueOf(1.01))
    }

    override fun getPair(pair: String): PairRate? {
        return pairs[pair]
    }

    override fun providerKey(): String {
        return "usdtusd"
    }

    override fun getRates(): Map<String, PairRate> {
        return pairs
    }
}