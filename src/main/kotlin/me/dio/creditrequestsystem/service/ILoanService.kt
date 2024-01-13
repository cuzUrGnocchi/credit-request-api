package me.dio.creditrequestsystem.service

import me.dio.creditrequestsystem.model.Loan
import java.util.UUID

interface ILoanService {
    fun save(credit: Loan): Loan
    fun findAllByCustomer(customerId: Long): List<Loan>
    fun findByCreditCode(customerId: Long, creditCode: UUID): Loan
}