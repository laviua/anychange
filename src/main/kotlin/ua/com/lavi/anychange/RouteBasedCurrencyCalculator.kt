package ua.com.lavi.anychange

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ua.com.lavi.anychange.exception.UnsupportedConversionException
import ua.com.lavi.anychange.model.CurrencyRoute
import ua.com.lavi.anychange.model.PairRate
import ua.com.lavi.anychange.provider.CurrencyProvider
import java.math.BigDecimal

class RouteBasedCurrencyCalculator(private val providers: Map<String, CurrencyProvider>,
                                   private val routes: List<CurrencyRoute>) : CurrencyCalculator {

    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    override fun rate(amount: BigDecimal, fromCurrency: String, toCurrency: String): BigDecimal {
        val requestedPair = fromCurrency + toCurrency
        if (log.isTraceEnabled) {
            log.trace("Exchange: $fromCurrency > $toCurrency")
        }
        val route = matchRoute(fromCurrency, toCurrency)

        // when route has been found, we can go step by step by direction pairs

        var rate = BigDecimal.ONE

        for (direction in route.directions) {
            val provider = providers[direction.provider] ?: error("Provider not found")

            val pairRate = getRate(provider, direction.pair)
            if (pairRate.pair() == requestedPair) {
                val price = pairRate.ask
                if (log.isTraceEnabled) {
                    log.trace("Direction: ${direction.pair}. Ask price: $price. Result: $rate")
                }

                rate = rate.multiply(price)
            } else {
                val price = pairRate.bid
                rate = rate.multiply(price)
                if (log.isTraceEnabled) {
                    log.trace("Direction: ${direction.pair}. Bid price: $price. Result: $rate")
                }
            }
        }
        return amount.multiply(rate)
    }

    private fun getRate(provider: CurrencyProvider, lookupPair: String): PairRate {
        for (rate in provider.getRates().values) {

            val baseQuotePair = rate.baseAsset + rate.quoteAsset
            if (lookupPair == baseQuotePair) {
                return rate
            }

            val quoteBasePair = rate.quoteAsset + rate.baseAsset
            if (lookupPair == quoteBasePair) {
                return rate
            }
        }
        throw UnsupportedConversionException()
    }

    private fun matchRoute(fromCurrency: String, toCurrency: String): CurrencyRoute {
        for (route in routes) {
            if (fromCurrency == route.baseAsset && toCurrency == route.quoteAsset) {
                if (log.isTraceEnabled) {
                    log.trace("Found route: $route")
                }
                return route
            }
            if (fromCurrency == route.quoteAsset && toCurrency == route.baseAsset) {
                if (log.isTraceEnabled) {
                    log.trace("Found route: $route")
                }
                return route
            }
        }
        throw UnsupportedConversionException()
    }
}