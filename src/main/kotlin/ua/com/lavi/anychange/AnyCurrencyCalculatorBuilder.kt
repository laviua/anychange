package ua.com.lavi.anychange

import ua.com.lavi.anychange.exception.EmptyProvidersException
import ua.com.lavi.anychange.exception.EmptyRoutesException
import ua.com.lavi.anychange.exception.UnknownCalculatorTypeException
import ua.com.lavi.anychange.exception.UnsupportedCalculatorTypeException
import ua.com.lavi.anychange.model.CalculatorType
import ua.com.lavi.anychange.model.CurrencyRoute
import ua.com.lavi.anychange.provider.AnyCurrencyProvider

class AnyCurrencyCalculatorBuilder {

    private val providers = hashMapOf<String, AnyCurrencyProvider>()
    private val routes = arrayListOf<CurrencyRoute>()
    private var calculatorType: CalculatorType? = null

    fun build(): RouteBasedCurrencyCalculator {

        if (calculatorType == null) {
            throw UnknownCalculatorTypeException()
        }
        if (providers.isEmpty()) {
            throw EmptyProvidersException()
        }

        when (calculatorType) {
            CalculatorType.ROUTE_BASED -> {
                if (routes.isEmpty()) {
                    throw EmptyRoutesException()
                }
                return RouteBasedCurrencyCalculator(providers, routes)
            }
            else -> throw UnsupportedCalculatorTypeException()
        }
    }

    fun addProvider(provider: AnyCurrencyProvider): AnyCurrencyCalculatorBuilder {
        this.providers[provider.key()] = provider
        return this
    }

    fun providers(providers: List<AnyCurrencyProvider>): AnyCurrencyCalculatorBuilder {
        for (provider in providers) {
            this.providers[provider.key()] = provider
        }
        return this
    }

    fun addRoute(route: CurrencyRoute): AnyCurrencyCalculatorBuilder {
        this.routes.add(route)
        return this
    }

    fun type(calculatorType: CalculatorType): AnyCurrencyCalculatorBuilder {
        this.calculatorType = calculatorType
        return this
    }

}