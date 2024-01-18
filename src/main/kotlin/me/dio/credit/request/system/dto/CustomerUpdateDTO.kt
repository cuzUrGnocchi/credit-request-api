package me.dio.credit.request.system.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import me.dio.credit.request.system.model.Address
import me.dio.credit.request.system.model.Customer
import java.math.BigDecimal

data class CustomerUpdateDTO(
    @field:NotEmpty(message = "firstName cannot be empty")
    val firstName: String,

    @field:NotEmpty(message = "lastName cannot be empty")
    val lastName: String,

    @field:NotNull(message = "income cannot be null")
    val income: BigDecimal,

    @field:NotEmpty(message = "zipCode cannot be empty")
    val zipCode: String,

    @field:NotEmpty(message = "street cannot be empty")
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
