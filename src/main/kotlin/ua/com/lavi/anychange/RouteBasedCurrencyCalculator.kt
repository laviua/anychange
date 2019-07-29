package ua.com.lavi.anychange

import ua.com.lavi.anychange.exception.UnsupportedConversionException
import ua.com.lavi.anychange.model.AnychangeSide
import ua.com.lavi.anychange.model.CurrencyPairRate
import ua.com.lavi.anychange.model.CurrencyRoute
import ua.com.lavi.anychange.provider.AnyCurrencyProvider
import java.math.BigDecimal

class RouteBasedCurrencyCalculator(val providers: Map<String, AnyCurrencyProvider>,
                                   val routes: Collection<CurrencyRoute>) : AnyCurrencyCalculator {

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

    override fun rate(symbolPair: String): CurrencyPairRate? {
        return rates().find { it.matchesPair(symbolPair) }
    }

    override fun exchange(symbolPair: String, side: AnychangeSide, amount: BigDecimal): BigDecimal {
        val rate = rate(symbolPair) ?: throw UnsupportedConversionException()

        if (side == AnychangeSide.BUY) {
            return amount.multiply(rate.ask)
        } else {
            return amount.multiply(rate.bid)
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

}