package ua.com.lavi.anychange.provider.stub

import ua.com.lavi.anychange.provider.AnyCurrencyProvider
import ua.com.lavi.anychange.model.CurrencyPairRate
import java.math.BigDecimal

class FakeBinanceCurrencyProvider : AnyCurrencyProvider {

    private val pairs = hashMapOf<String, CurrencyPairRate>()

    init {
        pairs["BTCUSDT"] = CurrencyPairRate("BTC", "USDT", BigDecimal.valueOf(10080.54), BigDecimal.valueOf(10083.78), 8)
        pairs["ETHUSDT"] = CurrencyPairRate("ETH", "USDT", BigDecimal.valueOf(221.57), BigDecimal.valueOf(221.65), 8)
        pairs["ETHBTC"] = CurrencyPairRate("ETH", "BTC", BigDecimal.valueOf(0.02142500), BigDecimal.valueOf(0.02142700), 8)
    }

    override fun key(): String {
        return "binance"
    }

    override fun getRates(): Map<String, CurrencyPairRate> {
        return pairs
    }

    override fun getRate(pair: String): CurrencyPairRate? {
        return pairs[pair]
    }
}