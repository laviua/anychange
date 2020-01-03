package ua.com.lavi.anychange.exception

class ProviderPairNotFoundException(provider: String, pair: String) : RuntimeException("$provider - $pair is not found")