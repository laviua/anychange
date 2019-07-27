package ua.com.lavi.anychange.model

data class CurrencyRoute(val baseAsset: String,
                         val quoteAsset: String,
                         val directionCurrencies: List<CurrencyRouteDirection>)