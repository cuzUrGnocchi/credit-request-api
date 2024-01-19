package me.dio.credit.request.system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import me.dio.credit.request.system.repository.CustomerRepository
import me.dio.credit.request.system.repository.LoanRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest
@ActiveProfiles("test")
@AutoConfiguration
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

    companion object {
        const val URL: String = "/api/loans"
    }

    @AfterEach
    fun tearDown() {
        customerRepository.deleteAll()
        loanRepository.deleteAll()
    }

    @Test
    fun `should register a loan and return a message containing the credit code and the customer's email`() {

    }
}