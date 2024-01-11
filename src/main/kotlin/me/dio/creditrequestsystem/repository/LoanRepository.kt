package me.dio.creditrequestsystem.repository

import me.dio.creditrequestsystem.model.Loan
import org.springframework.data.jpa.repository.JpaRepository

interface LoanRepository: JpaRepository<Loan, Long>