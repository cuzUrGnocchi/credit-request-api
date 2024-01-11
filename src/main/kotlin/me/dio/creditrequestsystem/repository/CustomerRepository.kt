package me.dio.creditrequestsystem.repository

import me.dio.creditrequestsystem.model.Customer
import org.springframework.data.jpa.repository.JpaRepository

interface CustomerRepository: JpaRepository<Customer, Long>