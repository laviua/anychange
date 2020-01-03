package ua.com.lavi.anychange.exception

class UnsupportedRoutePairException(val pair: String) : RuntimeException("Pair: $pair is not routed")