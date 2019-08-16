package ua.com.lavi.anychange

import ua.com.lavi.anychange.exception.EmptyAssetException
import ua.com.lavi.anychange.model.CurrencyRoute
import ua.com.lavi.anychange.model.CurrencyRouteDirection
import java.math.BigDecimal
import java.math.RoundingMode

class CurrencyRouteBuilder {

    private var baseAsset: String = ""
    private var quoteAsset: String = ""
    private var scale: Int = 30
    private var roundingMode: RoundingMode = RoundingMode.HALF_EVEN

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
     * Set scale for ask/bid rates
     */
    fun scale(scale: Int): CurrencyRouteBuilder {
        this.scale = scale
        return this
    }

    /**
     * Set rounding mode for scaling
     */
    fun roundingMode(roundingMode: RoundingMode): CurrencyRouteBuilder {
        this.roundingMode = roundingMode
        return this
    }

    /**
     * Build calculator
     */
    fun build(): CurrencyRoute {
        if (baseAsset.isEmpty() || quoteAsset.isEmpty()) {
            throw EmptyAssetException()
        }
        return CurrencyRoute(baseAsset = baseAsset, quoteAsset = quoteAsset, directions = directions, scale = scale, roundingMode = roundingMode)
    }
}