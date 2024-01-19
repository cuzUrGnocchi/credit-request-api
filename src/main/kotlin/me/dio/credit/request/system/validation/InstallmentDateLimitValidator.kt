package me.dio.credit.request.system.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import java.time.LocalDate

class InstallmentDateLimitValidator : ConstraintValidator<InstallmentDateLimit, LocalDate> {
    override fun isValid(value: LocalDate, context: ConstraintValidatorContext?): Boolean {
        return value.isBefore(LocalDate.now().plusMonths(3))
    }
}