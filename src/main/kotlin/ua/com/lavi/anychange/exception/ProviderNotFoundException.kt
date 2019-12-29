package ua.com.lavi.anychange.exception

import java.lang.RuntimeException

class ProviderNotFoundException(key: String) : RuntimeException("Provider: $key is not found")