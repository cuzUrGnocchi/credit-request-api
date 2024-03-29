package me.dio.credit.request.system.service.impl

import jakarta.persistence.EntityNotFoundException
import me.dio.credit.request.system.model.Customer
import me.dio.credit.request.system.repository.CustomerRepository
import org.springframework.stereotype.Service
import me.dio.credit.request.system.service.ICustomerService

@Service
class CustomerService(
    private val customerRepository: CustomerRepository
): ICustomerService {
    override fun save(customer: Customer): Customer =
        customerRepository.save(customer)

    override fun findById(id: Long): Customer =
        customerRepository.findById(id).orElseThrow {
            throw EntityNotFoundException("Customer of id $id not found")
        }

    override fun delete(id: Long) =
        findById(id).let {
            customerRepository.delete(it)
        }
}