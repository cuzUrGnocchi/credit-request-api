package me.dio.creditrequestsystem.controller

import jakarta.validation.Valid
import me.dio.creditrequestsystem.dto.LoanDTO
import me.dio.creditrequestsystem.dto.LoanListItemView
import me.dio.creditrequestsystem.dto.LoanView
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import me.dio.creditrequestsystem.service.impl.LoanService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.UUID
import java.util.stream.Collectors

@RestController
@RequestMapping("/api/loans")
class LoanController(
    private val loanService: LoanService
) {
    @PostMapping
    fun saveLoan(@RequestBody @Valid valueToStore: LoanDTO): ResponseEntity<String> =
        loanService.save(valueToStore.toEntity()).run {
            ResponseEntity.status(HttpStatus.CREATED).body("Loan $creditCode - Customer ${customer?.email} saved!")
        }

    @GetMapping
    fun findAllByCustomerId(@RequestParam(value = "customerId") customerId: Long): ResponseEntity<List<LoanListItemView>> =
        loanService.findAllByCustomer(customerId).let { loanList ->
            ResponseEntity.status(HttpStatus.OK)
                .body(loanList.stream().map { item -> LoanListItemView(item) }.collect(Collectors.toList()))
        }

    @GetMapping("/{creditCode}")
    fun findByCreditCode(
        @RequestParam(value = "customerId") customerId: Long, @PathVariable creditCode: UUID
    ): ResponseEntity<LoanView> = loanService.findByCreditCode(customerId, creditCode).let {
        ResponseEntity.status(HttpStatus.OK).body(LoanView(it))
    }

}