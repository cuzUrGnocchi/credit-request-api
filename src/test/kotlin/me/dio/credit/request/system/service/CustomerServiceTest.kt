package me.dio.credit.request.system.service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import me.dio.credit.request.system.enummeration.Status
import me.dio.credit.request.system.exception.BusinessException
import me.dio.credit.request.system.model.Address
import me.dio.credit.request.system.model.Customer
import me.dio.credit.request.system.model.Loan
import me.dio.credit.request.system.repository.CustomerRepository
import me.dio.credit.request.system.service.impl.CustomerService
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
class CustomerServiceTest {
    @MockK lateinit var customerRepository: CustomerRepository
    @InjectMockKs lateinit var customerService: CustomerService

    @Test
    fun `should create customer`() {
        val fakeCustomer: Customer = buildCustomer()

        every { customerRepository.save(any()) } returns fakeCustomer

        val actual: Customer = customerService.save(fakeCustomer)

        Assertions.assertThat(actual).isNotNull()

        verify(exactly = 1) { customerRepository.save(fakeCustomer) }
    }

    @Test
    fun `should find customer by id`() {
        val fakeId = Random.nextLong()
        val fakeCustomer = Customer(id = fakeId)

        every { customerRepository.findById(fakeId) } returns Optional.of(fakeCustomer)

        val actual = customerService.findById(fakeId)

        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isExactlyInstanceOf(Customer::class.java)
        Assertions.assertThat(actual).isSameAs(fakeCustomer)

        verify(exactly = 1) { customerRepository.findById(fakeId) }
    }

    @Test
    fun `should throw business exception when given id of non existing customer`() {
        val fakeId = Random.nextLong()

        every { customerRepository.findById(fakeId) } returns Optional.empty()

        Assertions.assertThatExceptionOfType(BusinessException::class.java)
            .isThrownBy { customerService.findById(fakeId) }
            .withMessage("Id $fakeId not found")

        verify(exactly = 1) { customerRepository.findById(fakeId) }
    }

    @Test
    fun `should delete customer by id`() {
        val fakeId = Random.nextLong()
        val fakeCustomer = buildCustomer(id = fakeId)

        every { customerRepository.findById(fakeId) } returns Optional.of(fakeCustomer)
        every { customerRepository.delete(fakeCustomer) } just runs

        customerService.delete(fakeId)

        verify(exactly = 1) { customerRepository.findById(fakeId) }
        verify(exactly = 1) { customerRepository.delete(fakeCustomer) }
    }

    private fun buildCustomer(
        firstName: String = "Cami",
        lastName: String = "Cavalcante",
        cpf: String = "28475934625",
        email: String = "camila@gmail.com",
        income: BigDecimal = BigDecimal.valueOf(1000.0),
        password: String = "12345",
        zipCode: String = "12345",
        street: String = "Rua da Cami",
        id: Long = 1L,
    ) = Customer(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        income = income,
        password = password,
        address = Address(zipCode = zipCode, street = street),
        id = id
    )

    private fun buildLoan(
        creditCode: UUID = UUID.randomUUID(),
        creditAmount: BigDecimal = generateNumberRandomly(decimalPlaces = 2),
        dateOfFirstInstallment: LocalDate = LocalDate.now().plusWeeks(generateNumberRandomly(lowerLimit = 0, upperLimit = 4).toLong()),
        numberOfInstallments: Int = generateNumberRandomly(lowerLimit = 1, upperLimit = 49).toInt(),
        status: Status = Status.PENDING,
        customer: Customer? = Customer(),
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

    private fun generateNumberRandomly(lowerLimit: Int = 0, upperLimit: Int = 10000, decimalPlaces: Int = 0): BigDecimal =
        BigDecimal.valueOf(lowerLimit + Math.random() * (upperLimit - lowerLimit)).setScale(decimalPlaces, RoundingMode.HALF_DOWN)
}