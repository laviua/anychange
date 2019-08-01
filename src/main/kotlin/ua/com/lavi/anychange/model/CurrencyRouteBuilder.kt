package ua.com.lavi.anychange.model

import ua.com.lavi.anychange.exception.EmptyAssetException
import java.math.BigDecimal

class CurrencyRouteBuilder {

    private var baseAsset: String = ""
    private var quoteAsset: String = ""

    private val directions = arrayListOf<CurrencyRouteDirection>()

    /**
     * Add route direction. Currency calculation will multiply every step
     */
    fun addDirection(pair: String, provider: String): CurrencyRouteBuilder {
        directions.add(CurrencyRouteDirection(pair, provider, BigDecimal.ZERO, false))
        return this
    }

    /**
     * Add route direction with correlation. It needs when we want to correlate according provider fee. Currency calculation will multiply every step.
     */
    fun addDirection(pair: String, provider: String, correlationPercent: BigDecimal): CurrencyRouteBuilder {
        directions.add(CurrencyRouteDirection(pair, provider, correlationPercent, false))
        return this
    }

    /**
     * Add full route direction with correlation and pair exchange direction
     */
    fun addDirection(pair: String, provider: String, reverse: Boolean): CurrencyRouteBuilder {
        directions.add(CurrencyRouteDirection(pair, provider, BigDecimal.ZERO, reverse))
        return this
    }

    /**
     * Add full route direction with correlation and pair exchange direction
     */
    fun addDirection(pair: String, provider: String, correlationPercent: BigDecimal, reverse: Boolean): CurrencyRouteBuilder {
        directions.add(CurrencyRouteDirection(pair, provider, correlationPercent, reverse))
        return this
    }

    /**
     * Add points for start and final destination during route converting.
     * Route should be matched by these currencies
     */
    fun addPoints(baseAsset: String, quoteAsset: String): CurrencyRouteBuilder {
        this.baseAsset = baseAsset
        this.quoteAsset = quoteAsset
        return this
    }

    /**
     * Build calculator
     */
    fun build(): CurrencyRoute {
        if (baseAsset.isEmpty() || quoteAsset.isEmpty()) {
            throw EmptyAssetException()
        }
        return CurrencyRoute(baseAsset, quoteAsset, directions)
    }
}