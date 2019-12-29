package ua.com.lavi.anychange.exception

import java.lang.RuntimeException

class UnsupportedRoutePairException(val pair: String) : RuntimeException("Pair: $pair is not routed")