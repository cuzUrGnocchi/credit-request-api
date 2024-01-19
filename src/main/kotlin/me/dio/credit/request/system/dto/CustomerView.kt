package me.dio.credit.request.system.dto

import io.swagger.v3.oas.annotations.media.Schema
import me.dio.credit.request.system.model.Customer
import java.math.BigDecimal

@Schema(description = "Model for the client's view of a customer.")
data class CustomerView(
    @field:Schema(example = "Camila")
    val firstName: String,

    @field:Schema(example = "Silva")

    val lastName: String,

    @field:Schema(example = "41693009048")
    val cpf: String,

    @field:Schema(example = "2500.0")
    val income: BigDecimal,

    @field:Schema(example = "camila@gmail.com")
    val email: String,

    @field:Schema(example = "04662")
    val zipCode: String,

    @field:Schema(example = "Rua da Camila, 205")
    val street: String,

    @field:Schema(example = "1")
    val id: Long?
) {
    constructor(customer: Customer) : this(
        firstName = customer.firstName,
        lastName = customer.lastName,
        cpf = customer.cpf,
        income = customer.income,
        email = customer.email,
        zipCode = customer.address.zipCode,
        street = customer.address.street,
        id = customer.id
    )
}
