package me.dio.credit.request.system.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.models.examples.Example
import jakarta.validation.Valid
import me.dio.credit.request.system.responses.ConstraintViolatedExample
import me.dio.credit.request.system.dto.CustomerDTO
import me.dio.credit.request.system.dto.CustomerUpdateDTO
import me.dio.credit.request.system.dto.CustomerView
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import me.dio.credit.request.system.service.impl.CustomerService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ResponseStatus

@RestController
@RequestMapping("/api/customers")
class CustomerController(
    private val customerService: CustomerService
) {
    companion object {
        const val CUSTOMER_NOT_FOUND_EXAMPLE_RESPONSE: String =
"""{
  {
    "title": "class jakarta.persistence.EntityNotFoundException",
    "timestamp": "2024-01-21T17:01:38.6786918",
    "status": 400,
    "exception": "class jakarta.persistence.EntityNotFoundException",
    "details": {
      "null": "Customer of id 1 not found"
    }
  }
}"""

        const val CONSTRAINT_VIOLATED_EXAMPLE_RESPONSE: String =
"""{
  {
    "title": "class org.springframework.dao.DataIntegrityViolationException",
    "timestamp": "2024-01-21T15:58:22.644751",
    "status": "409",
    "exception": "class org.springframework.dao.DataIntegrityViolationException",
    "details": "\"org.hibernate.exception.ConstraintViolationException: could not execute statement\": \"could not execute statement; SQL [n/a]; constraint [\\\"PUBLIC.UC_CUSTOMER_CPF_INDEX_5 ON PUBLIC.CUSTOMER(CPF NULLS FIRST) VALUES ( /* 1 */ '41693009048' )\\\"; SQL statement:\ninsert into customer (id, street, zip_code, cpf, email, first_name, income, last_name, password) values (default, ?, ?, ?, ?, ?, ?, ?, ?) [23505-214]]\""
  }
}"""
    }

    @PostMapping
    @Operation(summary = "Registers a customer")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Successful operation",
                content = [
                    (Content(mediaType = "application/json", array = (ArraySchema(schema = Schema(implementation = CustomerView::class)))))
                ]
            ),
            ApiResponse(
                responseCode = "409",
                description = "A constraint was violated",
                content = [
                    Content(examples = [ ExampleObject(value = CONSTRAINT_VIOLATED_EXAMPLE_RESPONSE) ])
                ]
            )
        ]
    )
    fun saveCustomer(@RequestBody @Valid customerDTO: CustomerDTO): ResponseEntity<CustomerView> =
        customerService.save(customerDTO.toEntity()).let {
            ResponseEntity.status(HttpStatus.CREATED).body(CustomerView(it))
        }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieves a customer")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successful operation",
                content = [
                    (Content(mediaType = "application/json", array = (ArraySchema(schema = Schema(implementation = CustomerView::class)))))
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Customer not found",
                content = [
                    Content(examples = [ ExampleObject(value = CUSTOMER_NOT_FOUND_EXAMPLE_RESPONSE) ])
                ]
            )
        ]
    )
    fun findById(@PathVariable id: Long): ResponseEntity<CustomerView> =
        customerService.findById(id).let {
            ResponseEntity
                .status(HttpStatus.OK)
                .body(CustomerView(it))
        }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deletes a customer")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "Successful operation"
            ),
            ApiResponse(
                responseCode = "400",
                description = "Customer not found",
                content = [
                    Content(examples = [ ExampleObject(value = CUSTOMER_NOT_FOUND_EXAMPLE_RESPONSE) ])
                ]
            )
        ]
    )
    fun deleteCustomer(@PathVariable id: Long) =
        customerService.delete(id)

    @PatchMapping
    @Operation(summary = "Updates a customer")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successful operation",
                content = [
                    (Content(mediaType = "application/json", array = (ArraySchema(schema = Schema(implementation = CustomerView::class)))))
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Customer not found",
                content = [
                    Content(examples = [ ExampleObject(value = CUSTOMER_NOT_FOUND_EXAMPLE_RESPONSE) ])
                ]
            ),
            ApiResponse(
                responseCode = "409",
                description = "A constraint was violated",
                content = [
                    Content(examples = [ ExampleObject(value = CONSTRAINT_VIOLATED_EXAMPLE_RESPONSE) ])
                ]
            )
        ]
    )
    fun updateCustomer(
        @RequestParam(value = "customerId") id: Long,
        @RequestBody @Valid customerUpdateDTO: CustomerUpdateDTO
    ): ResponseEntity<CustomerView> =
        customerService.findById(id).let { updateTarget ->
            customerService.save(customerUpdateDTO.toEntity(updateTarget)).let { updatedValue ->
                ResponseEntity
                    .status(HttpStatus.OK)
                    .body(CustomerView(updatedValue))
            }
        }
}