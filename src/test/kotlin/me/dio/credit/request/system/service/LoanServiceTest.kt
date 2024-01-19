package me.dio.credit.request.system.service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import me.dio.credit.request.system.enummeration.Status
import me.dio.credit.request.system.exception.BusinessException
import me.dio.credit.request.system.model.Customer
import me.dio.credit.request.system.model.Loan
import me.dio.credit.request.system.repository.CustomerRepository
import me.dio.credit.request.system.repository.LoanRepository
import me.dio.credit.request.system.service.impl.CustomerService
import me.dio.credit.request.system.service.impl.LoanService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.util.*
import kotlin.random.Random

@ExtendWith(MockKExtension::class)
class LoanServiceTest {
    @MockK lateinit var loanRepository: LoanRepository
    @InjectMockKs lateinit var loanService: LoanService

    @MockK lateinit var customerService: CustomerService

    @Test
    fun `save(loan) should return the loan that was given as an argument`() {
        val fakeLoan = buildLoan(customer = Customer(id = 0))

        every { loanRepository.save(fakeLoan) } returns fakeLoan
        every { customerService.findById(any()) } returns fakeLoan.customer

        val actual = loanService.save(fakeLoan)

        Assertions.assertThat(actual).isSameAs(fakeLoan)
        verify(exactly = 1) { loanRepository.save(fakeLoan) }
        verify(exactly = 1) { customerService.findById(any()) }
    }

    @Test
    fun `findAllByCustomer(customerId) should return a list of loans`() {
        val fakeCustomerId = Random.nextLong()
        val fakeLoans = arrayOf(0L, 1L, 2L, 3L).map { buildLoan(id = it, customer = Customer(fakeCustomerId)) }

        every { loanRepository.findAllByCustomer(fakeCustomerId) } returns fakeLoans

        val actual = loanService.findAllByCustomer(fakeCustomerId)

        Assertions.assertThat(actual).isSameAs(fakeLoans)
        verify(exactly = 1)  { loanRepository.findAllByCustomer(fakeCustomerId) }
    }

    @Test
    fun `findByCreditCode(customerId, creditCode) should return a loan`() {
        val fakeCreditCode = UUID.randomUUID()
        val fakeCustomerId = 0L
        val fakeLoan = buildLoan(creditCode = fakeCreditCode, customer = Customer(id = fakeCustomerId))

        every { loanRepository.findByCreditCode(fakeCreditCode) } returns fakeLoan

        val actual = loanService.findByCreditCode(fakeCustomerId, fakeCreditCode)

        Assertions.assertThat(actual).isSameAs(fakeLoan)
        verify(exactly = 1) { loanRepository.findByCreditCode(fakeCreditCode) }
    }

    @Test
    fun `findByCreditCode(customerId, creditCode) should throw a Business Exception when given a credit code that doesn't belong to any loan`() {
        val unassociatedCreditCode = UUID.randomUUID()

        every { loanRepository.findByCreditCode(unassociatedCreditCode) } returns null

        Assertions.assertThatExceptionOfType(BusinessException::class.java)
            .isThrownBy { loanService.findByCreditCode(customerId = 0L, unassociatedCreditCode) }
            .withMessage("Could not find any loan with credit code of $unassociatedCreditCode")

        verify(exactly = 1) { loanRepository.findByCreditCode(unassociatedCreditCode) }
    }

    @Test
    fun `findByCreditCode(customerId, creditCode) should throw an Illegal Argument Exception when the given customerId doesn't match that of the retrieved loan`() {
        val associatedCustomerId = 0L
        val associatedCreditCode = UUID.randomUUID()
        val fakeLoan = buildLoan(creditCode = associatedCreditCode, customer = Customer(id = associatedCustomerId))
        val unassociatedCustomerId = 1L

        every { loanRepository.findByCreditCode(associatedCreditCode) } returns fakeLoan

        Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java)
            .isThrownBy { loanService.findByCreditCode(unassociatedCustomerId, associatedCreditCode) }
            .withMessage("Customer id of $unassociatedCustomerId does not match that of the loan of credit code $associatedCreditCode")

        verify(exactly = 1) { loanRepository.findByCreditCode(associatedCreditCode) }
    }

    private fun generateNumberRandomly(lowerLimit: Int = 0, upperLimit: Int = 10000, decimalPlaces: Int = 0): BigDecimal =
        BigDecimal.valueOf(lowerLimit + Math.random() * (upperLimit - lowerLimit)).setScale(decimalPlaces, RoundingMode.HALF_DOWN)

    private fun buildLoan(
        creditCode: UUID = UUID.randomUUID(),
        creditAmount: BigDecimal = generateNumberRandomly(decimalPlaces = 2),
        dateOfFirstInstallment: LocalDate = LocalDate.now().plusWeeks(generateNumberRandomly(lowerLimit = 0, upperLimit = 4).toLong()),
        numberOfInstallments: Int = generateNumberRandomly(lowerLimit = 1, upperLimit = 49).toInt(),
        status: Status = Status.PENDING,
        customer: Customer,
        id: Long? = null
    ) = Loan(
        creditCode = creditCode,
        creditAmount = creditAmount,
        dateOfFirstInstallment = dateOfFirstInstallment,
        numberOfInstallments = numberOfInstallments,
        status = status,
        customer = customer,
        id = id
    )
}