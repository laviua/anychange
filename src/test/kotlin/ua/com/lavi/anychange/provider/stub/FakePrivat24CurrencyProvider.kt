package ua.com.lavi.anychange.provider.stub

import ua.com.lavi.anychange.model.ProviderPairRate
import ua.com.lavi.anychange.provider.AnyCurrencyProvider
import java.math.BigDecimal

class FakePrivat24CurrencyProvider : AnyCurrencyProvider {

    private val pairs = hashMapOf<String, ProviderPairRate>()

    init {
        pairs["EURUAH"] = ProviderPairRate("EUR", "UAH", BigDecimal.valueOf(28.20), BigDecimal.valueOf(28.81844))
        pairs["RUBUAH"] = ProviderPairRate("RUB", "UAH", BigDecimal.valueOf(0.370), BigDecimal.valueOf(0.41000))
        pairs["USDUAH"] = ProviderPairRate("USD", "UAH", BigDecimal.valueOf(25.35), BigDecimal.valueOf(25.50))
    }

    override fun getRate(pair: String): ProviderPairRate? {
        return pairs[pair]
    }

    override fun getRates(): Map<String, ProviderPairRate> {
        return pairs
    }

    override fun key(): String {
        return "privat24"
    }
}