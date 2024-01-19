package me.dio.credit.request.system.service.impl

import jakarta.persistence.*
import me.dio.credit.request.system.enummeration.Status
import me.dio.credit.request.system.exception.BusinessException
import me.dio.credit.request.system.model.Customer
import me.dio.credit.request.system.model.Loan
import me.dio.credit.request.system.repository.LoanRepository
import org.springframework.stereotype.Service
import me.dio.credit.request.system.service.ILoanService
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@Service
class LoanService(
    private val loanRepository: LoanRepository,
    private val customerService: CustomerService
): ILoanService {
    override fun save(loan: Loan): Loan =
        customerService.findById(loan.customer.id!!).let { customer ->
            loanRepository.save(loan.copy(customer = customer))
        }

    override fun findAllByCustomer(customerId: Long): List<Loan> =
        loanRepository.findAllByCustomer(customerId)

    override fun findByCreditCode(customerId: Long, creditCode: UUID): Loan {
        val loan: Loan? = loanRepository.findByCreditCode(creditCode)

        if (loan == null) {
            throw BusinessException("Could not find any loan with credit code of $creditCode")
        }

        if (loan.customer.id != customerId) {
            throw IllegalArgumentException("Customer id of $customerId does not match that of the loan of credit code $creditCode")
        }

        return loan
    }

}