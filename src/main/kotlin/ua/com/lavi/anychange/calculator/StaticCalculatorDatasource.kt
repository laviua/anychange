package ua.com.lavi.anychange.calculator

import ua.com.lavi.anychange.model.CurrencyRoute
import ua.com.lavi.anychange.provider.AnyCurrencyProvider

class StaticCalculatorDatasource(private val providers: Map<String, AnyCurrencyProvider>,
                                 private val routes: Map<String, CurrencyRoute>) : CalculatorDatasource {

    override fun providers(): Map<String, AnyCurrencyProvider> {
        return providers
    }

    override fun routes(): Map<String, CurrencyRoute> {
        return routes
    }
}