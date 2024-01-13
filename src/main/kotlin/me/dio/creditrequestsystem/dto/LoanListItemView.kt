package me.dio.creditrequestsystem.dto

import me.dio.creditrequestsystem.model.Loan
import java.math.BigDecimal
import java.util.UUID

data class LoanListItemView(
    val creditCode: UUID,
    val creditAmount: BigDecimal,
    val numberOfInstallments: Int
) {
    constructor(loan: Loan): this (
        creditCode = loan.creditCode,
        creditAmount = loan.creditAmount,
        numberOfInstallments = loan.numberOfInstallments
    )
}