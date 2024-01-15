package me.dio.credit.request.system.dto

import me.dio.credit.request.system.model.Loan
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