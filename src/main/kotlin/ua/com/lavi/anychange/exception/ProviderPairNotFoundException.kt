package ua.com.lavi.anychange.exception

import java.lang.RuntimeException

class ProviderPairNotFoundException(provider: String, pair: String) : RuntimeException("$provider - $pair is not found")