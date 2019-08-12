package ua.com.lavi.anychange.provider.stub

import ua.com.lavi.anychange.provider.AnyCurrencyProvider
import ua.com.lavi.anychange.model.CurrencyPairRate
import java.math.BigDecimal

class FakePrivat24CurrencyProvider : AnyCurrencyProvider {

    private val pairs = hashMapOf<String, CurrencyPairRate>()

    init {
        pairs["EURUAH"] = CurrencyPairRate("EUR", "UAH", BigDecimal.valueOf(28.20), BigDecimal.valueOf(28.81844))
        pairs["RUBUAH"] = CurrencyPairRate("RUB", "UAH", BigDecimal.valueOf(0.370), BigDecimal.valueOf(0.41000))
        pairs["USDUAH"] = CurrencyPairRate("USD", "UAH", BigDecimal.valueOf(25.35), BigDecimal.valueOf(25.50))
    }

    override fun getRate(pair: String): CurrencyPairRate? {
      return pairs[pair]
    }

    override fun getRates(): Map<String, CurrencyPairRate> {
        return pairs
    }

    override fun key(): String {
        return "privat24"
    }
}