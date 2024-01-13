package me.dio.creditrequestsystem.dto

import me.dio.creditrequestsystem.model.Customer
import me.dio.creditrequestsystem.model.Loan
import java.math.BigDecimal
import java.time.LocalDate

data class LoanDTO(
    val creditAmount: BigDecimal,
    val dateOfFirstInstallment: LocalDate,
    val numberOfInstallments: Int,
    val customerId: Long
) {
    fun toEntity(): Loan = Loan(
        creditAmount = this.creditAmount,
        dateOfFirstInstallment = this.dateOfFirstInstallment,
        numberOfInstallments =  this.numberOfInstallments,
        customer = Customer(id = this.customerId)
    )
}
