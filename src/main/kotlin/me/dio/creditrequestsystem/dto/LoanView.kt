package me.dio.creditrequestsystem.dto

import me.dio.creditrequestsystem.enummeration.Status
import me.dio.creditrequestsystem.model.Loan
import java.math.BigDecimal
import java.util.UUID

data class LoanView(
    val creditCode: UUID,
    val creditAmount: BigDecimal,
    val numberOfInstallments: Int,
    val status: Status,
    val email: String?,
    val customerIncome: BigDecimal?
) {
    constructor(loan: Loan): this(
        creditCode = loan.creditCode,
        creditAmount = loan.creditAmount,
        numberOfInstallments = loan.numberOfInstallments,
        status = loan.status,
        email = loan.customer?.email,
        customerIncome = loan.customer?.income
    )
}
