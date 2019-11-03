package ua.com.lavi.anychange.exception

import java.lang.RuntimeException

class InvalidFeeException : RuntimeException("Fee less or equals zero")