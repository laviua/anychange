package ua.com.lavi.anychange.calculator

import ua.com.lavi.anychange.exception.UnsupportedConversionException
import ua.com.lavi.anychange.model.ExchangeSide
import ua.com.lavi.anychange.model.CurrencyPairRate
import ua.com.lavi.anychange.model.CurrencyRoute
import ua.com.lavi.anychange.model.ExchangeRequest
import ua.com.lavi.anychange.provider.AnyCurrencyProvider
import java.math.BigDecimal

class RouteBasedAnyCurrencyCalculator(val providers: Map<String, AnyCurrencyProvider>,
                                      val routes: Collection<CurrencyRoute>) : AnyCurrencyCalculator {

    private fun BigDecimal.percentOf(value: BigDecimal): BigDecimal = this.multiply(value).divide(BigDecimal.valueOf(100)).stripTrailingZeros()

    override fun rates(): List<CurrencyPairRate> {
        val result = arrayListOf<CurrencyPairRate>()
        for (route in routes) {
            var bidRate = BigDecimal.ONE
            var askRate = BigDecimal.ONE
            for (direction in route.directions) {
                val pairRate = matchPair(direction.provider, direction.pair)
                if (direction.reverse) {
                    bidRate = bidRate.multiply(BigDecimal.ONE.divide(pairRate.ask, route.scale, route.roundingMode))
                    askRate = askRate.multiply(BigDecimal.ONE.divide(pairRate.bid, route.scale, route.roundingMode))

                    bidRate = bidRate.plus(direction.correlationPercent.percentOf(bidRate))
                    askRate = askRate.plus(direction.correlationPercent.percentOf(askRate))
                } else {
                    bidRate = bidRate.multiply(pairRate.bid)
                    askRate = askRate.multiply(pairRate.ask)
                    bidRate = bidRate.plus(direction.correlationPercent.percentOf(bidRate))
                    askRate = askRate.plus(direction.correlationPercent.percentOf(askRate))
                }
            }
            result.add(CurrencyPairRate(route.baseAsset, route.quoteAsset, bid = bidRate.setScale(route.scale, route.roundingMode), ask = askRate.setScale(route.scale, route.roundingMode)))
        }
        return result
    }

    override fun rate(symbolPair: String): CurrencyPairRate? {
        return rates().find { it.matchesPair(symbolPair) }
    }

    override fun exchange(symbolPair: String, side: ExchangeSide, amount: BigDecimal): BigDecimal {
        val rate = rate(symbolPair) ?: throw UnsupportedConversionException()

        if (side == ExchangeSide.BUY) {
            return amount.multiply(rate.ask)
        } else {
            return amount.multiply(rate.bid)
        }
    }

    override fun exchange(exchangeRequest: ExchangeRequest): BigDecimal {
        return exchange(exchangeRequest.symbolPair, exchangeRequest.side, exchangeRequest.amount)
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