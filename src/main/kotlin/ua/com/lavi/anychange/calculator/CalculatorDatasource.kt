package ua.com.lavi.anychange.calculator

import ua.com.lavi.anychange.model.CurrencyRoute
import ua.com.lavi.anychange.provider.AnyCurrencyProvider

interface CalculatorDatasource {
    fun providers() : Map<String, AnyCurrencyProvider>
    fun routes() : Map<String, CurrencyRoute>
}