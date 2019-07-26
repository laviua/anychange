package stub

import ua.com.lavi.anychange.provider.AnyCurrencyProvider
import ua.com.lavi.anychange.model.PairRate
import java.math.BigDecimal

class FakePrivat24CurrencyProvider : AnyCurrencyProvider {

    private val pairs = hashMapOf<String, PairRate>()

    init {
        pairs["UAHEUR"] = PairRate("UAH", "EUR", BigDecimal.valueOf(28.20), BigDecimal.valueOf(28.81844))
        pairs["UAHRUB"] = PairRate("UAH", "RUB", BigDecimal.valueOf(0.370), BigDecimal.valueOf(0.41000))
        pairs["UAHUSD"] = PairRate("UAH", "USD", BigDecimal.valueOf(25.35), BigDecimal.valueOf(25.50))
    }

    override fun getPair(pair: String): PairRate? {
      return pairs[pair]
    }

    override fun getRates(): Map<String, PairRate> {
        return pairs
    }

    override fun key(): String {
        return "privat24"
    }
}