package ua.com.lavi.anychange

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ua.com.lavi.anychange.exception.UnsupportedConversionException
import ua.com.lavi.anychange.model.CurrencyRoute
import ua.com.lavi.anychange.model.CurrencyPairRate
import ua.com.lavi.anychange.provider.AnyCurrencyProvider
import java.math.BigDecimal
import java.math.RoundingMode

class RouteBasedCurrencyCalculator(private val providers: Map<String, AnyCurrencyProvider>,
                                   private val routes: List<CurrencyRoute>) : AnyCurrencyCalculator {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

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

    override fun rate(amount: BigDecimal, fromCurrency: String, toCurrency: String): BigDecimal {
        val requestedPair = fromCurrency + toCurrency
        if (log.isTraceEnabled) {
            log.trace("Exchange: $amount $fromCurrency > $toCurrency")
        }
        val route = matchRoute(fromCurrency, toCurrency)
        val forwardRoute = route.baseAsset == fromCurrency

        if (log.isTraceEnabled) {
            log.trace("Found route: $route")
        }
        // when route has been found, we can go step by step by direction pairs
        var rate = BigDecimal.ONE

        for (direction in route.directions) {
            val pairRate = matchPair(direction.provider, direction.pair)

            // direct
            if (pairRate.pair == requestedPair) {
                val askPrice = pairRate.ask
                val price = BigDecimal.ONE.divide(askPrice, 30, RoundingMode.HALF_EVEN)
                if (log.isTraceEnabled) {
                    log.trace("Direct: ${direction.pair}. Ask price: $askPrice")
                }

                rate = rate.multiply(price)
            }

            // direct reversed
            else if (pairRate.reversedPair() == requestedPair) {
                val bidPrice = pairRate.bid
                rate = rate.multiply(bidPrice)
                if (log.isTraceEnabled) {
                    log.trace("Direct: ${direction.pair}. Bid price: $bidPrice")
                }
            } else {
                if (log.isTraceEnabled) {
                    log.trace("Cross: $pairRate")
                }
                if (forwardRoute) {
                    val bidPrice = pairRate.bid
                    rate = rate.multiply(bidPrice)
                } else {
                    val askPrice = pairRate.ask
                    val price = BigDecimal.ONE.divide(askPrice, 30, RoundingMode.HALF_EVEN)
                    rate = rate.multiply(price)
                }
            }
            rate = rate.plus(direction.correlationPercent.percentOf(rate))
        }

        val multiply = amount.multiply(rate)
        if (log.isTraceEnabled) {
            log.trace("Result: $multiply")
        }
        return multiply
    }

    private fun matchPair(providerKey: String, lookupPair: String): CurrencyPairRate {
        val provider = providers[providerKey] ?: error("Provider $providerKey is not found")

        for (rate in provider.getRates().values) {
            if (lookupPair == rate.baseAsset + rate.quoteAsset || lookupPair == rate.quoteAsset + rate.baseAsset) {
                return rate
            }
        }
        throw UnsupportedConversionException()
    }

    private fun matchRoute(fromCurrency: String, toCurrency: String): CurrencyRoute {
        for (route in routes) {
            if (fromCurrency == route.baseAsset && toCurrency == route.quoteAsset) {
                return route
            }
            if (fromCurrency == route.quoteAsset && toCurrency == route.baseAsset) {
                return route
            }
        }
        throw UnsupportedConversionException()
    }
}