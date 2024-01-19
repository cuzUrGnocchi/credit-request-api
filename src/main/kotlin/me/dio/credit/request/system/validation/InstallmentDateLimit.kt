package me.dio.credit.request.system.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.annotation.Target
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [InstallmentDateLimitValidator::class])
@MustBeDocumented
annotation class InstallmentDateLimit(
        val message: String = "Date of first installment can't exceed three months from its negotiation",
        val groups: Array<KClass<Any>> = [],
        val payload: Array<KClass<Payload>> = []
)