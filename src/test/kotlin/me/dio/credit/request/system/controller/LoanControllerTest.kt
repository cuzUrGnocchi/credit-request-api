package me.dio.credit.request.system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.*
import jdk.jfr.DataAmount
import me.dio.credit.request.system.dto.CustomerUpdateDTO
import me.dio.credit.request.system.dto.LoanDTO
import me.dio.credit.request.system.dto.LoanUpdateDTO
import me.dio.credit.request.system.enummeration.Status
import me.dio.credit.request.system.model.Address
import me.dio.credit.request.system.model.Customer
import me.dio.credit.request.system.model.Loan
import me.dio.credit.request.system.repository.CustomerRepository
import me.dio.credit.request.system.repository.LoanRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DataAccessException
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.web.bind.MethodArgumentNotValidException
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class LoanControllerTest {
    @Autowired
    private lateinit var loanRepository: LoanRepository

    @Autowired
    private lateinit var customerRepository: CustomerRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var customer: Customer

    companion object {
        const val URL: String = "/api/loans"
    }

    @BeforeEach
    fun setup() {
        customer = customerRepository.save(buildCustomer())
    }

    @AfterEach
    fun tearDown() {
        customerRepository.deleteAll()
        loanRepository.deleteAll()
    }

    @Test
    fun `should register a loan and return a message containing the credit code and the customer's email`() {
        val result: ResultActions = mockMvc.perform(
            MockMvcRequestBuilders
                .post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildLoanDTO(customer.id!!))))

        with(result) {
            val creditCode: UUID = loanRepository.findAll()[0].creditCode

            andExpect(MockMvcResultMatchers.status().isCreated)
            andExpect(MockMvcResultMatchers.content()
                .string("Loan $creditCode - Customer ${customer.email} saved!"))
            andDo(MockMvcResultHandlers.print())
        }
    }

    @Test
    fun `should not register loan whose customer is not registered, returning status code 400`() {
        mockMvc.perform(
            MockMvcRequestBuilders
                .post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildLoanDTO(customerId = 0))))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(EntityNotFoundException::class.java.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception")
                .value(EntityNotFoundException::class.java.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not register loan with a first installment date that's in the past, returning status code 400`() {
        mockMvc.perform(
            MockMvcRequestBuilders
                .post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildLoanDTO(
                    customerId = 0, dateOfFirstInstallment = LocalDate.now().minusWeeks(1)))))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(MethodArgumentNotValidException::class.java.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception")
                .value(MethodArgumentNotValidException::class.java.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should retrieve all loans belonging to a customer`() {
        val loans: Array<Loan> = arrayOf(
            Loan(
                customer = customer,
                creditAmount = BigDecimal.valueOf(2000.0),
                dateOfFirstInstallment = LocalDate.now().plusDays(15),
                numberOfInstallments = 6),
            Loan(
                customer = customer,
                creditAmount = BigDecimal.valueOf(2300.0),
                dateOfFirstInstallment = LocalDate.now().plusWeeks(3),
                numberOfInstallments = 15),
            Loan(
                customer = customer,
                creditAmount = BigDecimal.valueOf(5700.0),
                dateOfFirstInstallment = LocalDate.now().plusWeeks(1),
                numberOfInstallments = 24),
        )

        loans.forEach { loanRepository.save(it) }

        val result: ResultActions = mockMvc
            .perform(MockMvcRequestBuilders.get("$URL?customerId=${customer.id}"))

        loans.forEachIndexed { index, loan ->
            result
                .andExpect(MockMvcResultMatchers.jsonPath("$[$index].creditAmount").value(loan.creditAmount))
                .andExpect(MockMvcResultMatchers.jsonPath("$[$index].numberOfInstallments").value(loan.numberOfInstallments))
                .andExpect(MockMvcResultMatchers.jsonPath("$[$index].creditCode").isNotEmpty)
        }

        result
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should return status code 400 when trying to find loans of non existing customer`() {
        mockMvc
            .perform(MockMvcRequestBuilders.get("$URL?customerId=1"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(EntityNotFoundException::class.java.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception")
                .value(EntityNotFoundException::class.java.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should return a detailed view of the loan`() {
        val loan: Loan = loanRepository.save(Loan(
            creditAmount = BigDecimal.valueOf(5000.0),
            dateOfFirstInstallment = LocalDate.now().plusMonths(1),
            numberOfInstallments =  12,
            customer = customer
        ))

        mockMvc
            .perform(MockMvcRequestBuilders.get("$URL/${loan.creditCode}?customerId=${customer.id}"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditCode").value(loan.creditCode.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditAmount").value(loan.creditAmount))
            .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfInstallments").value(loan.numberOfInstallments))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(loan.status.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(loan.customer.email))
            .andExpect(MockMvcResultMatchers.jsonPath("$.customerIncome").value(loan.customer.income))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not return loan when given non corresponding customer id`() {
        val loan: Loan = loanRepository.save(Loan(
            creditAmount = BigDecimal.valueOf(5000.0),
            dateOfFirstInstallment = LocalDate.now().plusMonths(1),
            numberOfInstallments =  12,
            customer = customer
        ))

        val unrelatedCustomerId: Long = customerRepository.save(Customer(
            firstName = "Joana",
            lastName = "Lorraine",
            cpf = "58712223077",
            email ="joana@gmail.com",
            income = BigDecimal.valueOf(3750.0),
            password = "5SeNh4a",
            address = Address(zipCode = "49012", street = "Rua da Joana, 1501")
        )).id!!

        mockMvc
            .perform(MockMvcRequestBuilders.get("$URL/${loan.creditCode}?customerId=${unrelatedCustomerId}"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(IllegalArgumentException::class.java.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception")
                .value(IllegalArgumentException::class.java.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should indicate success with an empty response`() {
        val loan: Loan = loanRepository.save(Loan(
            creditAmount = BigDecimal.valueOf(5000.0),
            dateOfFirstInstallment = LocalDate.now().plusMonths(1),
            numberOfInstallments =  12,
            customer = customer
        ))

        mockMvc
            .perform(MockMvcRequestBuilders.delete("$URL/${loan.id}"))
            .andExpect(MockMvcResultMatchers.status().isNoContent)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not delete loan, informing specified id is not in the records`() {
        mockMvc
            .perform(MockMvcRequestBuilders.delete("$URL/1"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(EntityNotFoundException::class.java.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception")
                .value(EntityNotFoundException::class.java.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should update loan`() {
        val loan: Loan = loanRepository.save(Loan(
            creditAmount = BigDecimal.valueOf(5000.0),
            dateOfFirstInstallment = LocalDate.now().plusMonths(1),
            numberOfInstallments =  12,
            customer = customer
        ))

        val updateDTO = LoanUpdateDTO(status = Status.APPROVED)

        mockMvc
            .perform(
                MockMvcRequestBuilders
                    .patch("$URL?customerId=${loan.customer.id}&creditCode=${loan.creditCode}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateDTO)))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditCode").value(loan.creditCode.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditAmount").value(loan.creditAmount))
            .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfInstallments").value(loan.numberOfInstallments))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(updateDTO.status.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(loan.customer.email))
            .andExpect(MockMvcResultMatchers.jsonPath("$.customerIncome").value(loan.customer.income))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not update loan when given a non corresponding customer id, returning status code 400`() {
        val loan: Loan = loanRepository.save(Loan(
            creditAmount = BigDecimal.valueOf(5000.0),
            dateOfFirstInstallment = LocalDate.now().plusMonths(1),
            numberOfInstallments =  12,
            customer = customer
        ))

        val updateDTO = LoanUpdateDTO(status = Status.APPROVED)

        val unrelatedCustomerId: Long = customerRepository.save(Customer(
            firstName = "Joana",
            lastName = "Lorraine",
            cpf = "58712223077",
            email ="joana@gmail.com",
            income = BigDecimal.valueOf(3750.0),
            password = "5SeNh4a",
            address = Address(zipCode = "49012", street = "Rua da Joana, 1501")
        )).id!!

        mockMvc
            .perform(MockMvcRequestBuilders
                .patch("$URL?customerId=$unrelatedCustomerId&creditCode=${loan.creditCode}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(IllegalArgumentException::class.java.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception")
                .value(IllegalArgumentException::class.java.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    private fun buildLoanDTO(customerId: Long,
                             creditAmount: BigDecimal = BigDecimal.valueOf(5000.0),
                             dateOfFirstInstallment: LocalDate = LocalDate.now().plusMonths(1),
                             numberOfInstallments: Int =  12): LoanDTO =
        LoanDTO(
            creditAmount = creditAmount,
            dateOfFirstInstallment = dateOfFirstInstallment,
            numberOfInstallments =  numberOfInstallments,
            customerId = customerId
        )

    private fun buildCustomer(): Customer =
        Customer(
            firstName = "Camila",
            lastName = "Silva",
            cpf = "41693009048",
            email ="camila@gmail.com",
            income = BigDecimal.valueOf(2500.0),
            password = "P4s5wORd",
            address = Address(zipCode = "04662", street = "Rua da Camila, 205")
        )
}