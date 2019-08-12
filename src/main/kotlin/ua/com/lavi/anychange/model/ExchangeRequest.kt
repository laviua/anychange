package ua.com.lavi.anychange.model

import java.math.BigDecimal

data class ExchangeRequest(val symbolPair: String,
                           val side: ExchangeSide,
                           val amount: BigDecimal)