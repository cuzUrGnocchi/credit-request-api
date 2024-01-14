package me.dio.creditrequestsystem.service.impl

import me.dio.creditrequestsystem.exception.BusinessException
import me.dio.creditrequestsystem.model.Customer
import me.dio.creditrequestsystem.repository.CustomerRepository
import org.springframework.stereotype.Service
import me.dio.creditrequestsystem.service.ICustomerService

@Service
class CustomerService(
    private val customerRepository: CustomerRepository
): ICustomerService {
    override fun save(customer: Customer): Customer =
        customerRepository.save(customer)

    override fun findById(id: Long): Customer =
        customerRepository.findById(id).orElseThrow {
            throw BusinessException("Id $id not found")
        }

    override fun delete(id: Long) =
        findById(id).let {
            customerRepository.delete(it)
        }
}