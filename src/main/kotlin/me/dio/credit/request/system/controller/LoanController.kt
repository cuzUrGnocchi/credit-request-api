package me.dio.credit.request.system.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import me.dio.credit.request.system.dto.*
import me.dio.credit.request.system.service.impl.LoanService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID
import java.util.stream.Collectors

@RestController
@RequestMapping("/api/loans")
class LoanController(
    private val loanService: LoanService
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

        const val LOAN_NOT_FOUND_EXAMPLE_RESPONSE: String =
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
    @Operation(summary = "Registers a loan")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Successful operation",
                content = [
                    (Content(examples = [ExampleObject(value = "Loan 751b5701-9d46-4875-a6f7-95742a6faa27 - Customer camila@gmail.com saved!")]))
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
    fun saveLoan(@RequestBody @Valid loanDTO: LoanDTO): ResponseEntity<String> =
        loanService.save(loanDTO.toEntity()).run {
            ResponseEntity.status(HttpStatus.CREATED).body("Loan $creditCode - Customer ${customer.email} saved!")
        }

    @GetMapping
    @Operation(summary = "Retrieves all loans belonging to a customer")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successful operation",
                content = [
                    (Content(mediaType = "application/json", array = (ArraySchema(schema = Schema(implementation = LoanListItemView::class)))))
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Loan not found",
                content = [
                    Content(examples = [ ExampleObject(value = LOAN_NOT_FOUND_EXAMPLE_RESPONSE) ])
                ]
            )
        ]
    )
    fun findAllByCustomerId(@RequestParam(value = "customerId") customerId: Long): ResponseEntity<List<LoanListItemView>> =
        loanService.findAllByCustomer(customerId).let { loanList ->
            ResponseEntity.status(HttpStatus.OK)
                .body(loanList.stream().map { item -> LoanListItemView(item) }.collect(Collectors.toList()))
        }

    @GetMapping("/{creditCode}")
    @Operation(summary = "Retrieves a loan")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successful operation",
                content = [
                    (Content(mediaType = "application/json", array = (ArraySchema(schema = Schema(implementation = LoanView::class)))))
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Loan or customer not found",
                content = [
                    Content(examples = [
                        ExampleObject(value = LOAN_NOT_FOUND_EXAMPLE_RESPONSE),
                        ExampleObject(value = CUSTOMER_NOT_FOUND_EXAMPLE_RESPONSE)
                    ])
                ]
            )
        ]
    )
    fun findByCreditCode(
        @RequestParam(value = "customerId") customerId: Long, @PathVariable creditCode: UUID
    ): ResponseEntity<LoanView> = loanService.findByCreditCode(customerId, creditCode).let {
        ResponseEntity.status(HttpStatus.OK).body(LoanView(it))
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @Operation(summary = "Deletes a loan")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "Successful operation"
            ),
            ApiResponse(
                responseCode = "400",
                description = "Loan not found",
                content = [
                    Content(examples = [ ExampleObject(value = LOAN_NOT_FOUND_EXAMPLE_RESPONSE) ])
                ]
            )
        ]
    )
    fun deleteLoan(@PathVariable id: Long) {
        loanService.delete(id)
    }

    @PatchMapping
    @Operation(summary = "Updates a loan")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successful operation",
                content = [
                    (Content(
                        mediaType = "application/json",
                        array = (ArraySchema(schema = Schema(implementation = LoanView::class)))
                    ))
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Loan or customer not found",
                content = [
                    Content(
                        examples = [
                            ExampleObject(value = LOAN_NOT_FOUND_EXAMPLE_RESPONSE),
                            ExampleObject(value = CUSTOMER_NOT_FOUND_EXAMPLE_RESPONSE)
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "409",
                description = "A constraint was violated",
                content = [
                    Content(examples = [ExampleObject(value = CONSTRAINT_VIOLATED_EXAMPLE_RESPONSE)])
                ]
            )
        ]
    )
    fun updateLoan(
        @RequestParam(value = "customerId") customerId: Long,
        @RequestParam(value = "creditCode") creditCode: UUID,
        @RequestBody @Valid loanUpdateDTO: LoanUpdateDTO
    ): ResponseEntity<LoanView> {
        return loanService.findByCreditCode(customerId, creditCode).let { updateTarget ->
            loanService.save(loanUpdateDTO.toEntity(updateTarget)).let { updatedValue ->
                ResponseEntity
                    .status(HttpStatus.OK)
                    .body(LoanView(updatedValue))
            }
        }
    }
}