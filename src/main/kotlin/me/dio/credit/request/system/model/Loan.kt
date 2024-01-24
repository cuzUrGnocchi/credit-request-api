package me.dio.credit.request.system.model

import jakarta.persistence.*
import me.dio.credit.request.system.enummeration.Status
import org.hibernate.proxy.HibernateProxy
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@Entity
data class Loan(
    @Column(nullable = false, unique = true)
    val creditCode: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val creditAmount: BigDecimal,

    @Column(nullable = false)
    val dateOfFirstInstallment: LocalDate,

    @Column(nullable = false)
    val numberOfInstallments: Int,

    @Enumerated
    var status: Status = Status.PENDING,

    @ManyToOne
    val customer: Customer,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
)
