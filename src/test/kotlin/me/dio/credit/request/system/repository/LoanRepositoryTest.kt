package me.dio.credit.request.system.repository

import me.dio.credit.request.system.enummeration.Status
import me.dio.credit.request.system.model.Address
import me.dio.credit.request.system.model.Customer
import me.dio.credit.request.system.model.Loan
import org.assertj.core.api.Assertions

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.util.*

@ActiveProfiles("Test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LoanRepositoryTest {
    @Autowired
    lateinit var loanRepository: LoanRepository

    @Autowired
    lateinit var testEntityManager: TestEntityManager

    private lateinit var customer: Customer
    private lateinit var loans: List<Loan>

    @BeforeEach
    fun setup() {
        customer = testEntityManager.persist(buildCustomer())
        loans = listOf(
            testEntityManager.persist(buildLoan(customer = customer)),
            testEntityManager.persist(buildLoan(customer = customer))
        )
    }

    @Test
    fun `findByCreditCode(creditCode) should return the loan of given creditCode`() {
        val creditCodes = loans.map { it.creditCode }

        val actualLoans = creditCodes.map { loanRepository.findByCreditCode(it) }

        Assertions.assertThat(actualLoans).isNotEmpty
        Assertions.assertThat(actualLoans).isEqualTo(loans)
    }

    @Test
    fun `findAllByCustomer(customerId) should return all the loans associated to the given customerId`() {
        val customerId = customer.id!!

        val actual = loanRepository.findAllByCustomer(customerId)

        Assertions.assertThat(actual).isNotEmpty
        Assertions.assertThat(actual).isEqualTo(loans)
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
    ) = Customer(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        income = income,
        password = password,
        address = Address(zipCode = zipCode, street = street)
    )

    private fun generateNumberRandomly(lowerLimit: Int = 0, upperLimit: Int = 10000, decimalPlaces: Int = 0): BigDecimal =
        BigDecimal.valueOf(lowerLimit + Math.random() * (upperLimit - lowerLimit)).setScale(decimalPlaces, RoundingMode.HALF_DOWN)

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
}