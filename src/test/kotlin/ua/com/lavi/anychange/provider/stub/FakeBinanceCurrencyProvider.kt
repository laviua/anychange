package ua.com.lavi.anychange.provider.stub

import ua.com.lavi.anychange.provider.AnyCurrencyProvider
import ua.com.lavi.anychange.model.ProviderPairRate
import java.math.BigDecimal

class FakeBinanceCurrencyProvider : AnyCurrencyProvider {

    private val pairs = hashMapOf<String, ProviderPairRate>()

    init {
        pairs["BTCUSDT"] = ProviderPairRate("BTC", "USDT", BigDecimal.valueOf(10080.54), BigDecimal.valueOf(10083.78))
        pairs["ETHUSDT"] = ProviderPairRate("ETH", "USDT", BigDecimal.valueOf(221.57), BigDecimal.valueOf(221.65))
        pairs["ETHBTC"] = ProviderPairRate("ETH", "BTC", BigDecimal.valueOf(0.02142500), BigDecimal.valueOf(0.02142700))
    }

    override fun key(): String {
        return "binance"
    }

    override fun getRates(): Map<String, ProviderPairRate> {
        return pairs
    }

    override fun getRate(pair: String): ProviderPairRate? {
        return pairs[pair]
    }
}