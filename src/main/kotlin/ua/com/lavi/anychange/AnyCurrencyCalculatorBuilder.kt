package ua.com.lavi.anychange

import ua.com.lavi.anychange.exception.EmptyProvidersException
import ua.com.lavi.anychange.exception.EmptyRoutesException
import ua.com.lavi.anychange.exception.UnknownCalculatorTypeException
import ua.com.lavi.anychange.exception.UnsupportedCalculatorTypeException
import ua.com.lavi.anychange.model.CurrencyCalculatorType
import ua.com.lavi.anychange.model.CurrencyRoute
import ua.com.lavi.anychange.provider.AnyCurrencyProvider

class AnyCurrencyCalculatorBuilder {

    private val providers = hashMapOf<String, AnyCurrencyProvider>()
    private val routes = arrayListOf<CurrencyRoute>()
    private var calculatorType: CurrencyCalculatorType? = null

    fun build(): RouteBasedCurrencyCalculator {

        if (calculatorType == null) {
            throw UnknownCalculatorTypeException()
        }
        if (providers.isEmpty()) {
            throw EmptyProvidersException()
        }

        when (calculatorType) {
            CurrencyCalculatorType.ROUTE_BASED -> {
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

    fun addRoute(route: CurrencyRoute): AnyCurrencyCalculatorBuilder {
        this.routes.add(route)
        return this
    }

    fun addProviders(providers: Collection<AnyCurrencyProvider>): AnyCurrencyCalculatorBuilder {
        for (provider in providers) {
            this.providers[provider.key()] = provider
        }
        return this
    }

    fun addRoutes(routes: Collection<CurrencyRoute>): AnyCurrencyCalculatorBuilder {
        for (route in routes) {
            this.routes.add(route)
        }
        return this
    }

    fun type(calculatorType: CurrencyCalculatorType): AnyCurrencyCalculatorBuilder {
        this.calculatorType = calculatorType
        return this
    }

}