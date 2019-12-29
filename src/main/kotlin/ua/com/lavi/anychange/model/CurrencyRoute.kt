package ua.com.lavi.anychange.model

import java.math.RoundingMode

data class CurrencyRoute(val baseAsset: String,
                         val quoteAsset: String,
                         val scale: Int,
                         val roundingMode: RoundingMode,
                         val directions: List<CurrencyRouteDirection>,
                         val pair: String = baseAsset + quoteAsset)