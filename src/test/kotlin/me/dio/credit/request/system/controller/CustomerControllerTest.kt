package me.dio.credit.request.system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.EntityNotFoundException
import me.dio.credit.request.system.dto.CustomerDTO
import me.dio.credit.request.system.dto.CustomerUpdateDTO
import me.dio.credit.request.system.model.Customer
import me.dio.credit.request.system.repository.CustomerRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DataAccessException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.web.bind.MethodArgumentNotValidException
import java.math.BigDecimal

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CustomerControllerTest {
    @Autowired
    private lateinit var customerRepository: CustomerRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    companion object {
        const val URL: String = "/api/customers"
    }

    @AfterEach
    fun tearDown() = customerRepository.deleteAll()

    @Test
    fun `should store a customer and return status code 201`() {
        val customerDTO: CustomerDTO = buildCustomerDTO()

        customerDTO.run {
            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(this)))
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(firstName))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(lastName))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value(cpf))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(email))
                .andExpect(MockMvcResultMatchers.jsonPath("$.income").value(income))
                .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value(zipCode))
                .andExpect(MockMvcResultMatchers.jsonPath("$.street").value(street))
                .andDo(MockMvcResultHandlers.print())
        }
    }

    @Test
    fun `should not store customers with same cpf, returning status code 409`() {
        val customerDTO: CustomerDTO = buildCustomerDTO()

        customerRepository.save(customerDTO.toEntity())

        mockMvc
            .perform(
                MockMvcRequestBuilders
                    .post(URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(customerDTO)))
            .andExpect(MockMvcResultMatchers.status().isConflict)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(DataIntegrityViolationException::class.java.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(409))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception")
                .value(DataIntegrityViolationException::class.java.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not store customers without firstName and return status code 400`() {
        val customerDTO: CustomerDTO = buildCustomerDTO(firstName = "")

        mockMvc
            .perform(
                MockMvcRequestBuilders
                    .post(URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(customerDTO)))
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
    fun `should find customer`() {
        customerRepository.save(buildCustomerDTO().toEntity()). run {
            mockMvc
                .perform(MockMvcRequestBuilders.get("$URL/${id}"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(firstName))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(lastName))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value(cpf))
                .andExpect(MockMvcResultMatchers.jsonPath("$.income").value(income))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(email))
                .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value(address.zipCode))
                .andExpect(MockMvcResultMatchers.jsonPath("$.street").value(address.street))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id))
                .andDo(MockMvcResultHandlers.print())
        }
    }

    @Test
    fun `should not find customer, returning exception details with status code 400`() {
        mockMvc
            .perform(MockMvcRequestBuilders.get("$URL/0"))
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
    fun `should indicate success with an empty response`() {
        val id: Long = customerRepository.save(buildCustomerDTO().toEntity()).id!!

        mockMvc
            .perform(MockMvcRequestBuilders.delete("$URL/$id"))
            .andExpect(MockMvcResultMatchers.status().isNoContent)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not delete customer, informing specified id is not in the records`() {
        mockMvc
            .perform(MockMvcRequestBuilders.delete("$URL/0"))
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
    fun `should update customer`() {
        val customer: Customer = customerRepository.save(buildCustomerDTO().toEntity())

        val updateDTO: CustomerUpdateDTO = CustomerUpdateDTO(
            firstName = "Cami",
            lastName = "Cavalcante dos Reis",
            income = BigDecimal.valueOf(2500.0),
            zipCode = "111111",
            street =  "Rua da Joaninha, 1910"
        )

        mockMvc
            .perform(
                MockMvcRequestBuilders
                    .patch("$URL?customerId=${customer.id}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateDTO)))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(updateDTO.firstName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(updateDTO.lastName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.income").value(updateDTO.income))
            .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value(updateDTO.zipCode))
            .andExpect(MockMvcResultMatchers.jsonPath("$.street").value(updateDTO.street))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value(customer.cpf))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(customer.email))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(customer.id))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not update customer, retuning exception details and status code 400`() {
        val updateDTO: CustomerUpdateDTO = CustomerUpdateDTO(
            firstName = "Cami",
            lastName = "Cavalcante dos Reis",
            income = BigDecimal.valueOf(2500.0),
            zipCode = "111111",
            street =  "Rua da Joaninha, 1910"
        )

        mockMvc
            .perform(MockMvcRequestBuilders
                .patch("$URL?customerId=1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception")
                .value(EntityNotFoundException::class.java.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    private fun buildCustomerDTO(
        firstName: String = "Camila",
        lastName: String = "Cavalcante",
        cpf: String = "28475934625",
        email: String = "camila@gmail.com",
        income: BigDecimal = BigDecimal.valueOf(1000.0),
        password: String = "1234",
        zipCode: String = "0000000",
        street: String = "Rua da Cami, 123",
    ) = CustomerDTO(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        income = income,
        password = password,
        zipCode = zipCode,
        street = street
    )
}