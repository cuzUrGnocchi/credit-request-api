package me.dio.credit.request.system.dto

import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import me.dio.credit.request.system.model.Customer
import me.dio.credit.request.system.model.Loan
import java.math.BigDecimal
import java.time.LocalDate

data class LoanDTO(
    @field:NotNull(message = "creditAmount cannot be empty")
    val creditAmount: BigDecimal,

    @field:NotNull(message = "dateOfFirstInstallment cannot be empty") @field:Future
    val dateOfFirstInstallment: LocalDate,

    @field:NotNull(message = "numberOfInstallments cannot be empty") @field:Positive
    val numberOfInstallments: Int,

    @field:NotNull(message = "customerId cannot be empty")
    val customerId: Long
) {
    fun toEntity(): Loan = Loan(
        creditAmount = this.creditAmount,
        dateOfFirstInstallment = this.dateOfFirstInstallment,
        numberOfInstallments =  this.numberOfInstallments,
        customer = Customer(customerId)
    )
}
