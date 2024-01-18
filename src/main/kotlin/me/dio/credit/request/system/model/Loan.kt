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
    var creditCode: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val creditAmount: BigDecimal = BigDecimal.ZERO,

    @Column(nullable = false)
    val dateOfFirstInstallment: LocalDate = LocalDate.now().plusWeeks(1),

    @Column(nullable = false)
    val numberOfInstallments: Int = 1,

    @Enumerated
    val status: Status = Status.PENDING,

    @ManyToOne
    var customer: Customer? = null,

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
