package ua.com.lavi.anychange.model

import java.math.BigDecimal

data class CurrencyRouteDirection(val pair: String,
                                  val provider: String,
                                  val correlationPercent: BigDecimal = BigDecimal.ZERO)