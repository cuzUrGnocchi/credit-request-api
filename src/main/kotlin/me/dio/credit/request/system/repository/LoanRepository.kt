package me.dio.credit.request.system.repository

import me.dio.credit.request.system.model.Loan
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface LoanRepository: JpaRepository<Loan, Long> {
    fun findByCreditCode(creditCode: UUID): Loan?

    @Query(value = "SELECT * FROM LOAN WHERE CUSTOMER_ID = ?1", nativeQuery = true)
    fun findAllByCustomer(customerId: Long): List<Loan>
}