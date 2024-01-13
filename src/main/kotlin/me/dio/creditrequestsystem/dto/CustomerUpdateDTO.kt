package me.dio.creditrequestsystem.dto

import me.dio.creditrequestsystem.model.Address
import me.dio.creditrequestsystem.model.Customer
import java.math.BigDecimal

data class CustomerUpdateDTO(
    val firstName: String,
    val lastName: String,
    val income: BigDecimal,
    val zipCode: String,
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
