package me.dio.credit.request.system.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import me.dio.credit.request.system.model.Address
import me.dio.credit.request.system.model.Customer
import java.math.BigDecimal

@Schema(description = "Data transfer object used for updating customer information.")
data class CustomerUpdateDTO(
    @field:NotEmpty(message = "Field firstName cannot be empty")
    @field:Schema(example = "Camilla")
    val firstName: String,

    @field:NotEmpty(message = "Field lastName cannot be empty")
    @field:Schema(example = "da Silva")
    val lastName: String,

    @field:NotNull(message = "Field income cannot be null")
    @field:Schema(example = "3500.0")
    @field:Positive
    val income: BigDecimal,

    @field:NotEmpty(message = "Field zipCode cannot be empty")
    @field:Schema(example = "14738")
    val zipCode: String,

    @field:NotEmpty(message = "Field street cannot be empty")
    @field:Schema(example = "Rua da Silva, 245")
    val street: String
) {
    fun toEntity(customer: Customer): Customer =
        customer.copy(
            firstName = this.firstName,
            lastName = this.lastName,
            income = this.income,
            address = Address(zipCode = this.zipCode, street = this.street)
        )
}
