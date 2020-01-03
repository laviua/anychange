package ua.com.lavi.anychange.exception

class ProviderNotFoundException(key: String) : RuntimeException("Provider: $key is not found")