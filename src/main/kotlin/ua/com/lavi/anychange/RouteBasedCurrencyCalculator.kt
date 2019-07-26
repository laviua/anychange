package ua.com.lavi.anychange

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ua.com.lavi.anychange.exception.UnsupportedConversionException
import ua.com.lavi.anychange.model.CurrencyRoute
import ua.com.lavi.anychange.model.PairRate
import ua.com.lavi.anychange.provider.AnyCurrencyProvider
import java.math.BigDecimal
import java.math.RoundingMode

class RouteBasedCurrencyCalculator(private val providers: Map<String, AnyCurrencyProvider>,
                                   private val routes: List<CurrencyRoute>) : AnyCurrencyCalculator {

    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

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

            val provider = providers[direction.provider] ?: error("Provider not found")

            val pairRate = matchPair(provider, direction.pair)
            // direct
            if (pairRate.pair() == requestedPair) {
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
        }

        val multiply = amount.multiply(rate)
        if (log.isTraceEnabled) {
            log.trace("Result: $multiply")
        }
        return multiply
    }

    private fun matchPair(provider: AnyCurrencyProvider, lookupPair: String): PairRate {
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