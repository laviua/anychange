package stub

import ua.com.lavi.anychange.provider.CurrencyProvider
import ua.com.lavi.anychange.model.PairRate
import java.math.BigDecimal

class FakeBinanceCurrencyProvider : CurrencyProvider {

    private val pairs = hashMapOf<String, PairRate>()

    init {
        pairs["BTCUSDT"] = PairRate("BTC", "USDT", BigDecimal.valueOf(10080.54), BigDecimal.valueOf(10083.78))
        pairs["ETHUSDT"] = PairRate("ETH", "USDT", BigDecimal.valueOf(221.57), BigDecimal.valueOf(221.65))
        pairs["ETHBTC"] = PairRate("ETH", "BTC", BigDecimal.valueOf(0.02197800), BigDecimal.valueOf(0.02198400))
    }

    override fun providerKey(): String {
        return "binance"
    }

    override fun getRates(): Map<String, PairRate> {
        return pairs
    }

    override fun getPair(pair: String): PairRate? {
        return pairs[pair]
    }
}