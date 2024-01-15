package me.dio.credit.request.system.repository

import me.dio.credit.request.system.model.Customer
import org.springframework.data.jpa.repository.JpaRepository

interface CustomerRepository: JpaRepository<Customer, Long>