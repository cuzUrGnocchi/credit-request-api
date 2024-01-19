package me.dio.credit.request.system.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import me.dio.credit.request.system.model.Address
import me.dio.credit.request.system.model.Customer
import org.hibernate.validator.constraints.br.CPF
import java.math.BigDecimal

@Schema(description = "Data transfer object used for registering a new customer.")
data class CustomerDTO(
    @field:NotEmpty(message = "Field firstName cannot be empty")
    @field:Schema(example = "Camila")
    val firstName: String,

    @field:NotEmpty(message = "Field lastName cannot be empty")
    @field:Schema(example = "Silva")
    val lastName: String,

    @field:NotEmpty(message = "Field cpf cannot be empty")
    @field:CPF(message = "Invalid CPF")
    @field:Schema(
        description = "Numbers only",
        example = "41693009048",
    )
    val cpf: String,

    @field:NotNull(message = "Field income cannot be null")
    @field:Positive
    @field:Schema(example = "2500.0")
    val income: BigDecimal,

    @field:NotEmpty(message = "Field email cannot be empty")
    @field:Email(message = "Invalid Email")
    @field:Schema(example = "camila@gmail.com")
    val email: String,

    @field:NotEmpty(message = "Field password cannot be empty")
    @field:Schema(example = "P4s5wORd")
    val password: String,

    @field:NotEmpty(message = "Field zipCode cannot be empty")
    @field:Schema(example = "04662")
    val zipCode: String,

    @field:NotEmpty(message = "Field street cannot be empty")
    @field:Schema(example = "Rua da Camila, 205")
    val street: String,
) {
    fun toEntity(): Customer = Customer(
        firstName = this.firstName,
        lastName = this.lastName,
        cpf = this.cpf,
        income = this.income,
        email = this.email,
        password = this.password,
        address = Address(
            zipCode = this.zipCode, street = this.street
        )
    )
}
