package ua.com.lavi.anychange

import ua.com.lavi.anychange.exception.UnsupportedConversionException
import ua.com.lavi.anychange.model.AnychangeResult
import ua.com.lavi.anychange.model.AnychangeSide
import ua.com.lavi.anychange.model.CurrencyPairRate
import ua.com.lavi.anychange.model.CurrencyRoute
import ua.com.lavi.anychange.provider.AnyCurrencyProvider
import java.math.BigDecimal

class RouteBasedCurrencyCalculator(private val providers: Map<String, AnyCurrencyProvider>,
                                   private val routes: Collection<CurrencyRoute>) : AnyCurrencyCalculator {

    private fun BigDecimal.percentOf(value: BigDecimal): BigDecimal = this.multiply(value).divide(BigDecimal.valueOf(100)).stripTrailingZeros()

    override fun rates(): List<CurrencyPairRate> {
        val result = arrayListOf<CurrencyPairRate>()
        for (route in routes) {
            var bidRate = BigDecimal.ONE
            var askRate = BigDecimal.ONE
            for (direction in route.directions) {
                val pairRate = matchPair(direction.provider, direction.pair)
                bidRate = bidRate.multiply(pairRate.bid)
                askRate = askRate.multiply(pairRate.ask)
                bidRate = bidRate.plus(direction.correlationPercent.percentOf(bidRate))
                askRate = askRate.plus(direction.correlationPercent.percentOf(askRate))
            }
            result.add(CurrencyPairRate(route.baseAsset, route.quoteAsset, bid = bidRate, ask = askRate))
        }
        return result
    }

    override fun calculate(amount: BigDecimal, fromCurrency: String, toCurrency: String): AnychangeResult {
        val rates = rates()
        val quoted = rates.find { it.matchesPair(fromCurrency + toCurrency) }
        if (quoted == null) {
            throw UnsupportedConversionException()
        } else {
            return AnychangeResult(toCurrency, amount.multiply(quoted.bid))
        }
    }

    override fun exchange(symbolPair: String, side: AnychangeSide, amount: BigDecimal): AnychangeResult {
        val rates = rates()
        val quoted = rates.find { it.matchesPair(symbolPair) } ?: throw UnsupportedConversionException()

        if (side == AnychangeSide.BUY) {
            return AnychangeResult(quoted.quoteAsset, amount.multiply(quoted.ask))
        } else {
            return AnychangeResult(quoted.quoteAsset, amount.multiply(quoted.bid))
        }
    }

    private fun matchPair(providerKey: String, lookupPair: String): CurrencyPairRate {
        val provider = providers[providerKey] ?: error("Provider $providerKey is not found")

        for (providerRate in provider.getRates().values) {
            if (providerRate.matchesPair(lookupPair)) {
                return providerRate
            }
        }
        throw UnsupportedConversionException()
    }

    fun getRoutes(): Collection<CurrencyRoute> {
        return routes
    }

    fun getProviders(): Map<String, AnyCurrencyProvider> {
        return providers
    }
}