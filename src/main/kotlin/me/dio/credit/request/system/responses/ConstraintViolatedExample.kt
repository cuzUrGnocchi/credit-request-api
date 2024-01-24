package me.dio.credit.request.system.responses

import io.swagger.v3.oas.annotations.media.Schema

data class ConstraintViolatedExample(
    @field:Schema(example = "class org.springframework.dao.DataIntegrityViolationException")
    val title: String,

    @field:Schema(example = "2024-01-21T15:58:22.644751")
    val timestamp: String,

    @field:Schema(example = "409")
    val status: String,

    @field:Schema(example = "class org.springframework.dao.DataIntegrityViolationException")
    val exception: String,

    @field:Schema(example = "\"org.hibernate.exception.ConstraintViolationException: could not execute statement\": \"could not execute statement; SQL [n/a]; constraint [\\\"PUBLIC.UC_CUSTOMER_CPF_INDEX_5 ON PUBLIC.CUSTOMER(CPF NULLS FIRST) VALUES ( /* 1 */ '41693009048' )\\\"; SQL statement:\ninsert into customer (id, street, zip_code, cpf, email, first_name, income, last_name, password) values (default, ?, ?, ?, ?, ?, ?, ?, ?) [23505-214]]\"")
    val details: String
)