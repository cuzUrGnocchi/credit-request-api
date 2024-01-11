package me.dio.creditrequestsystem.model

import jakarta.persistence.*
import me.dio.creditrequestsystem.enummeration.Status
import org.hibernate.proxy.HibernateProxy
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@Entity
data class Loan(
    @Column(nullable = false, unique = true)
    val loanCode: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val amount: BigDecimal = BigDecimal.ZERO,

    @Column(nullable = false)
    val dateOfFirstInstallment: LocalDate,

    @Column(nullable = false)
    val numberOfInstallments: Int = 0,

    @Enumerated
    val status: Status = Status.PENDING,

    @ManyToOne
    val customer: Customer? = null,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) {
    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass = this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as Loan

        return id != null && id == other.id
    }

    final override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }
}
