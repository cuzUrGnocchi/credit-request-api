package me.dio.credit.request.system.service.impl

import jakarta.persistence.EntityNotFoundException
import me.dio.credit.request.system.model.Customer
import me.dio.credit.request.system.model.Loan
import me.dio.credit.request.system.repository.LoanRepository
import org.springframework.stereotype.Service
import me.dio.credit.request.system.service.ILoanService
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
        loanRepository.findAllByCustomer(customerId).also {
            if (it.isEmpty()) throw EntityNotFoundException("Customer of id $customerId not found")
        }

    override fun findByCreditCode(customerId: Long, creditCode: UUID): Loan {
        val customer: Customer = customerService.findById(customerId)
        val loan: Loan? = loanRepository.findByCreditCode(creditCode)

        if (loan == null) {
            throw EntityNotFoundException("Could not find any loan with credit code of $creditCode")
        }

        if (loan.customer.id != customer.id) {
            throw IllegalArgumentException("Customer id of ${customer.id} does not match that of the loan of credit code $creditCode")
        }

        return loan
    }

    override fun delete(id: Long) {
        loanRepository.delete(loanRepository.findById(id)
            .orElseThrow { throw EntityNotFoundException("Loan of id $id not found") })
    }

}