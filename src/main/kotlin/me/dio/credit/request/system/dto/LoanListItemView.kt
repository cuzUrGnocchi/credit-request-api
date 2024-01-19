package me.dio.credit.request.system.dto

import io.swagger.v3.oas.annotations.media.Schema
import me.dio.credit.request.system.model.Loan
import java.math.BigDecimal
import java.util.UUID

data class LoanListItemView(
    @field:Schema(example = "fee99306-b1c1-4501-8b9f-332dff21fa62")
    val creditCode: UUID,

    @field:Schema(example = "5000.0")
    val creditAmount: BigDecimal,

    @field:Schema(example = "12")
    val numberOfInstallments: Int
) {
    constructor(loan: Loan): this (
        creditCode = loan.creditCode,
        creditAmount = loan.creditAmount,
        numberOfInstallments = loan.numberOfInstallments
    )
}