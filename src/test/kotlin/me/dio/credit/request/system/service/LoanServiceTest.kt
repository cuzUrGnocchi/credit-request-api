package me.dio.credit.request.system.service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import jakarta.persistence.EntityNotFoundException
import me.dio.credit.request.system.enummeration.Status
import me.dio.credit.request.system.model.Address
import me.dio.credit.request.system.model.Customer
import me.dio.credit.request.system.model.Loan
import me.dio.credit.request.system.repository.LoanRepository
import me.dio.credit.request.system.service.impl.CustomerService
import me.dio.credit.request.system.service.impl.LoanService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
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
    fun `save(loan) should return the loan with an actual customer object attached`() {
        val fakeLoan = buildLoan(customer = Customer(id = 0))

        val retrievedCustomer = Customer(
            firstName = "Camila",
            lastName = "Silva",
            cpf = "41693009048",
            email = "camila@gmail.com",
            income = BigDecimal.valueOf(2500.0),
            password = "P4s5wORd",
            address = Address("04662", "Rua da Camila, 205"),
            id = 0
        )

        val retrievedLoan = fakeLoan.copy(customer = retrievedCustomer)

        every { loanRepository.save(retrievedLoan) } returns retrievedLoan

        every { customerService.findById(0) } returns retrievedCustomer

        val actual = loanService.save(fakeLoan)

        Assertions.assertThat(actual).isSameAs(retrievedLoan)
        verify(exactly = 1) { loanRepository.save(retrievedLoan) }
        verify(exactly = 1) { customerService.findById(0) }
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
        val customer = Customer(id = 1)
        val loan = buildLoan(creditCode = UUID.randomUUID(), customer = customer)

        every { customerService.findById(customer.id!!) } returns customer
        every { loanRepository.findByCreditCode(loan.creditCode) } returns loan

        val actual = loanService.findByCreditCode(customer.id!!, loan.creditCode)

        Assertions.assertThat(actual).isSameAs(loan)
        verify(exactly = 1) { customerService.findById(customer.id!!) }
        verify(exactly = 1) { loanRepository.findByCreditCode(loan.creditCode) }
    }

    @Test
    fun `findByCreditCode(customerId, creditCode) should throw an Entity Not Found Exception when given a credit code that doesn't belong to any loan`() {
        val customer = Customer(id = 1)
        val unassociatedCreditCode: UUID = UUID.randomUUID()

        every { customerService.findById(customer.id!!) } returns customer
        every { loanRepository.findByCreditCode(unassociatedCreditCode) } returns null

        Assertions.assertThatExceptionOfType(EntityNotFoundException::class.java)
            .isThrownBy { loanService.findByCreditCode(customer.id!!, unassociatedCreditCode) }
            .withMessage("Could not find any loan with credit code of $unassociatedCreditCode")

        verify(exactly = 1) { customerService.findById(customer.id!!) }
        verify(exactly = 1) { loanRepository.findByCreditCode(unassociatedCreditCode) }
    }

    @Test
    fun `findByCreditCode(customerId, creditCode) should throw an Illegal Argument Exception when the given customerId doesn't match that of the retrieved loan`() {
        val customer1 = Customer(id = 1)
        val customer2 = Customer(id = 2)
        val loan = buildLoan(creditCode = UUID.randomUUID(), customer = Customer(id = 1L))

        every { customerService.findById(customer2.id!!) } returns customer2
        every { loanRepository.findByCreditCode(loan.creditCode) } returns loan

        Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java)
            .isThrownBy { loanService.findByCreditCode(customer2.id!!, loan.creditCode) }
            .withMessage("Customer id of ${customer2.id} does not match that of the loan of credit code ${loan.creditCode}")

        verify(exactly = 1) { loanRepository.findByCreditCode(loan.creditCode) }
    }

    @Test
    fun `should look for a loan to delete it afterwards`() {
        val loan: Loan = buildLoan(
            id = 1,
            customer = Customer(
                firstName = "Camila",
                lastName = "Silva",
                cpf = "41693009048",
                email = "camila@gmail.com",
                income = BigDecimal.valueOf(2500.0),
                password = "P4s5wORd",
                address = Address("04662", "Rua da Camila, 205"),
                id = 0
            )
        )

        every { loanRepository.findById(loan.id!!) } returns Optional.of(loan)
        every { loanRepository.delete(loan) } just runs

        loanService.delete(loan.id!!)

        verify(exactly = 1) { loanRepository.findById(loan.id!!) }
        verify(exactly = 1) { loanRepository.delete(loan) }
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