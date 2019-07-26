package ua.com.lavi.anychange.model

import ua.com.lavi.anychange.exception.EmptyAssetException

class CurrencyRouteBuilder {

    private var baseAsset: String = ""
    private var quoteAsset: String = ""

    private val directions = arrayListOf<RouteDirection>()

    fun addDirection(pair: String, provider: String): CurrencyRouteBuilder {
        directions.add(RouteDirection(pair, provider))
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

    fun build(): CurrencyRoute {
        if (baseAsset.isEmpty() || quoteAsset.isEmpty()) {
            throw EmptyAssetException()
        }
        return CurrencyRoute(baseAsset, quoteAsset, directions)
    }
}