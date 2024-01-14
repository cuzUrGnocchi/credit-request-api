package me.dio.creditrequestsystem.service.impl

import me.dio.creditrequestsystem.exception.BusinessException
import me.dio.creditrequestsystem.model.Loan
import me.dio.creditrequestsystem.repository.LoanRepository
import org.springframework.stereotype.Service
import me.dio.creditrequestsystem.service.ILoanService
import java.util.*

@Service
class LoanService(
    private val loanRepository: LoanRepository,
    private val customerService: CustomerService
): ILoanService {
    override fun save(credit: Loan): Loan {
        credit.apply {
            customer = customerService.findById(credit.customer?.id!!)
        }
        return this.loanRepository.save(credit)
    }

    override fun findAllByCustomer(customerId: Long): List<Loan> =
        this.loanRepository.findAllByCustomer(customerId)

    override fun findByCreditCode(customerId: Long, creditCode: UUID): Loan {
        return this.loanRepository.findByCreditCode(creditCode).run {
            if (this == null) throw BusinessException("Credit code $creditCode not found")
            if (this.customer?.id != customerId) throw IllegalArgumentException("Contact admin")
            this
        }
    }

}