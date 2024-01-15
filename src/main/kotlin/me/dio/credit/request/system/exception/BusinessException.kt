package me.dio.credit.request.system.exception

import java.lang.RuntimeException

data class BusinessException(override val message: String?) : RuntimeException(message)