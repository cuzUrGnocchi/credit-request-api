package me.dio.credit.request.system.dto

import me.dio.credit.request.system.enummeration.Status
import me.dio.credit.request.system.model.Loan

class LoanUpdateDTO (
    val status: Status
) {
    fun toEntity(loan: Loan): Loan = loan.copy(status = this.status)
}