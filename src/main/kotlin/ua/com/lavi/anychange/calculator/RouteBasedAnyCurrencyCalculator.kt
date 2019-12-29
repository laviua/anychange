package ua.com.lavi.anychange.calculator

import ua.com.lavi.anychange.exception.ProviderNotFoundException
import ua.com.lavi.anychange.exception.ProviderPairNotFoundException
import ua.com.lavi.anychange.exception.UnsupportedRoutePairException
import ua.com.lavi.anychange.model.*
import ua.com.lavi.anychange.provider.AnyCurrencyProvider
import java.math.BigDecimal

class RouteBasedAnyCurrencyCalculator(val providers: Map<String, AnyCurrencyProvider>,
                                      val routes: Map<String, CurrencyRoute>) : AnyCurrencyCalculator {

    private fun BigDecimal.percentOf(value: BigDecimal): BigDecimal = this.multiply(value).divide(BigDecimal.valueOf(100)).stripTrailingZeros()

    override fun rates(): List<CurrencyPairRate> {
        val result = arrayListOf<CurrencyPairRate>()
        for (route in routes.values) {
            result.add(buildRate(route))
        }
        return result
    }

    override fun rate(symbolPair: String): CurrencyPairRate {
        val route: CurrencyRoute = routes[symbolPair] ?: throw UnsupportedRoutePairException(symbolPair)
        val rate = buildRate(route)
        return rate
    }

    override fun exchange(symbolPair: String, side: ExchangeSide, amount: BigDecimal): BigDecimal {
        val rate = rate(symbolPair)

        if (side == ExchangeSide.BUY) {
            return amount.multiply(rate.ask)
        } else {
            return amount.multiply(rate.bid)
        }
    }

    override fun exchange(exchangeRequest: ExchangeRequest): BigDecimal {
        return exchange(exchangeRequest.symbolPair, exchangeRequest.side, exchangeRequest.amount)
    }

    private fun matchProviderPair(providerKey: String, lookupPair: String): ProviderPairRate {
        val provider = providers[providerKey] ?: throw ProviderNotFoundException(providerKey)

        for (providerRate in provider.getRates().values) {
            if (providerRate.matchesPair(lookupPair)) {
                return providerRate
            }
        }
        throw ProviderPairNotFoundException(providerKey, lookupPair)
    }

    private fun buildRate(route: CurrencyRoute): CurrencyPairRate {
        var bidRate = BigDecimal.ONE
        var askRate = BigDecimal.ONE
        for (direction in route.directions) {
            val pairRate = matchProviderPair(direction.provider, direction.pair)
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
        val currencyPairRate = CurrencyPairRate(
                baseAsset = route.baseAsset,
                quoteAsset = route.quoteAsset,
                bid = bidRate.setScale(route.scale, route.roundingMode).stripTrailingZeros(),
                ask = askRate.setScale(route.scale, route.roundingMode).stripTrailingZeros(),
                roundingMode = route.roundingMode,
                scale = route.scale)
        return currencyPairRate
    }
}
