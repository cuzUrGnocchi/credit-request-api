package me.dio.credit.request.system.service.impl

import me.dio.credit.request.system.exception.BusinessException
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
    override fun save(loan: Loan): Loan {
        loan.apply {
            customer = customerService.findById(loan.customer?.id!!)
        }
        return this.loanRepository.save(loan)
    }

    override fun findAllByCustomer(customerId: Long): List<Loan> =
        this.loanRepository.findAllByCustomer(customerId)

    override fun findByCreditCode(customerId: Long, creditCode: UUID): Loan {
        return this.loanRepository.findByCreditCode(creditCode).run {
            if (this == null) throw BusinessException("Could not find any loan with credit code of $creditCode")
            if (this.customer?.id != customerId)
                throw IllegalArgumentException("Customer id of $customerId does not match that of the loan of credit code $creditCode")
            this
        }
    }

}