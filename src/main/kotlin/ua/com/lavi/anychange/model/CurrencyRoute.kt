package ua.com.lavi.anychange.model

data class CurrencyRoute(val baseAsset: String,
                         val quoteAsset: String,
                         val directions: List<CurrencyRouteDirection>)