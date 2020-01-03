package ua.com.lavi.anychange

import java.math.BigDecimal

internal fun BigDecimal.percentOf(value: BigDecimal): BigDecimal = this.multiply(value).divide(BigDecimal.valueOf(100)).stripTrailingZeros()