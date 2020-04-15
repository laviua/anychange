package ua.com.lavi.anychange

import ua.com.lavi.anychange.calculator.StaticCalculatorDatasource
import ua.com.lavi.anychange.calculator.RouteBasedAnyCurrencyCalculator
import ua.com.lavi.anychange.exception.EmptyProvidersException
import ua.com.lavi.anychange.exception.EmptyRoutesException
import ua.com.lavi.anychange.exception.UnknownCalculatorTypeException
import ua.com.lavi.anychange.exception.UnsupportedCalculatorTypeException
import ua.com.lavi.anychange.model.CurrencyCalculatorType
import ua.com.lavi.anychange.model.CurrencyRoute
import ua.com.lavi.anychange.provider.AnyCurrencyProvider

class AnyCurrencyCalculatorBuilder {

    private val providers = hashMapOf<String, AnyCurrencyProvider>()
    private val routes = sortedMapOf<String, CurrencyRoute>()
    private var calculatorType: CurrencyCalculatorType? = null

    fun build(): RouteBasedAnyCurrencyCalculator {

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
                return RouteBasedAnyCurrencyCalculator(StaticCalculatorDatasource(providers, routes))
            }
            else -> throw UnsupportedCalculatorTypeException()
        }
    }

    fun addProvider(provider: AnyCurrencyProvider): AnyCurrencyCalculatorBuilder {
        this.providers[provider.key()] = provider
        return this
    }

    fun addRoute(route: CurrencyRoute): AnyCurrencyCalculatorBuilder {
        this.routes[route.pair] = route
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
            this.routes[route.pair] = route
        }
        return this
    }

    fun type(calculatorType: CurrencyCalculatorType): AnyCurrencyCalculatorBuilder {
        this.calculatorType = calculatorType
        return this
    }

}