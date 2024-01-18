package me.dio.credit.request.system.service

import me.dio.credit.request.system.model.Loan
import java.util.UUID

interface ILoanService {
    fun save(loan: Loan): Loan
    fun findAllByCustomer(customerId: Long): List<Loan>
    fun findByCreditCode(customerId: Long, creditCode: UUID): Loan
}