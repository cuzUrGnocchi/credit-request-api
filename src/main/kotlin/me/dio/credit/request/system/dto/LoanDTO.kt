package me.dio.credit.request.system.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import me.dio.credit.request.system.model.Customer
import me.dio.credit.request.system.model.Loan
import me.dio.credit.request.system.validation.InstallmentDateLimit
import java.math.BigDecimal
import java.time.LocalDate

@Schema(description = "Data transfer object for registering a loan.")
data class LoanDTO(
    @field:NotNull(message = "Field creditAmount cannot be empty")
    @field:Positive
    @field:Schema(description = "Value of the granted loan", example = "5000.0")
    val creditAmount: BigDecimal,

    @field:NotNull(message = "Field dateOfFirstInstallment cannot be empty")
    @field:FutureOrPresent
    @field:Schema(
        description = "The date for the first installment must be before 3 months from the day of its negotiation",
        example = "yyyy-mm-dd"
    )
    @InstallmentDateLimit
    val dateOfFirstInstallment: LocalDate,

    @field:NotNull(message = "Field numberOfInstallments cannot be empty")
    @field:Min(value = 1)
    @field:Max(value = 48)
    @field:Schema(
        description = "Maximum number of installments is 48",
        minimum = "1",
        maximum = "48",
        example = "12"
    )
    val numberOfInstallments: Int,

    @field:NotNull(message = "Field customerId cannot be empty")
    @field:Schema(example = "1")
    val customerId: Long
) {
    fun toEntity(): Loan = Loan(
        creditAmount = this.creditAmount,
        dateOfFirstInstallment = this.dateOfFirstInstallment,
        numberOfInstallments =  this.numberOfInstallments,
        customer = Customer(customerId)
    )
}
