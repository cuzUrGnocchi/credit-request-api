package me.dio.creditrequestsystem.controller

import me.dio.creditrequestsystem.dto.CustomerDTO
import me.dio.creditrequestsystem.dto.CustomerUpdateDTO
import me.dio.creditrequestsystem.dto.CustomerView
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import me.dio.creditrequestsystem.service.impl.CustomerService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

@RestController
@RequestMapping("/api/customers")
class CustomerController(
    private val customerService: CustomerService
) {
    @PostMapping
    fun saveCustomer(@RequestBody customerDTO: CustomerDTO): ResponseEntity<String> =
        customerService.save(customerDTO.toEntity()).run {
            ResponseEntity.status(HttpStatus.CREATED).body("Customer $email saved")
        }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<CustomerView> =
        customerService.findById(id).let {
            ResponseEntity
                .status(HttpStatus.OK)
                .body(CustomerView(it))
        }

    @DeleteMapping("/{id}")
    fun deleteCustomer(@PathVariable id: Long) =
        customerService.delete(id)

    @PatchMapping
    fun updateCustomer(
        @RequestParam(value = "customerId") id: Long,
        @RequestBody customerUpdateDTO: CustomerUpdateDTO
    ): ResponseEntity<CustomerView> =
        customerService.findById(id).let { updateTarget ->
            customerService.save(customerUpdateDTO.toEntity(updateTarget)).let { updatedValue ->
                ResponseEntity
                    .status(HttpStatus.OK)
                    .body(CustomerView(updatedValue))
            }
        }
}