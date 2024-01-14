package me.dio.creditrequestsystem.dto

import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotNull
import me.dio.creditrequestsystem.model.Customer
import me.dio.creditrequestsystem.model.Loan
import java.math.BigDecimal
import java.time.LocalDate

data class LoanDTO(
    @field:NotNull(message = "creditAmount cannot be empty") val creditAmount: BigDecimal,

    @field:NotNull(message = "dateOfFirstInstallment cannot be empty") @field:Future val dateOfFirstInstallment: LocalDate,

    @field:NotNull(message = "numberOfInstallments cannot be empty") val numberOfInstallments: Int,

    @field:NotNull(message = "customerId cannot be empty") val customerId: Long
) {
    fun toEntity(): Loan = Loan(
        creditAmount = this.creditAmount,
        dateOfFirstInstallment = this.dateOfFirstInstallment,
        numberOfInstallments =  this.numberOfInstallments,
        customer = Customer(id = this.customerId)
    )
}
