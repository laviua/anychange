package ua.com.lavi.anychange.provider.stub

import ua.com.lavi.anychange.provider.AnyCurrencyProvider
import ua.com.lavi.anychange.model.CurrencyPairRate
import java.math.BigDecimal

class FakeBinanceCurrencyProvider : AnyCurrencyProvider {

    private val pairs = hashMapOf<String, CurrencyPairRate>()

    init {
        pairs["BTCUSDT"] = CurrencyPairRate("BTC", "USDT", BigDecimal.valueOf(11513.98), BigDecimal.valueOf(11516.31))
        pairs["ETHUSDT"] = CurrencyPairRate("ETH", "USDT", BigDecimal.valueOf(221.57), BigDecimal.valueOf(221.65))
        pairs["ETHBTC"] = CurrencyPairRate("ETH", "BTC", BigDecimal.valueOf(0.02142500), BigDecimal.valueOf(0.02142700))
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