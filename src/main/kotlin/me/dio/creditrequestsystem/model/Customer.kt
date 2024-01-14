package me.dio.creditrequestsystem.model

import jakarta.persistence.*
import org.hibernate.proxy.HibernateProxy
import java.math.BigDecimal

@Entity
data class Customer(
    @Column(nullable = false) var firstName: String = "",

    @Column(nullable = false) var lastName: String = "",

    @Column(nullable = false, unique = true) val cpf: String = "",

    @Column(nullable = false, unique = true) var email: String = "",

    @Column(nullable = false) var income: BigDecimal = BigDecimal.ZERO,

    @Column(nullable = false) var password: String = "",

    @Embedded @Column(nullable = false) val address: Address = Address(),

    @Column(nullable = false) @OneToMany(
        fetch = FetchType.LAZY, cascade = [CascadeType.REMOVE, CascadeType.PERSIST], mappedBy = "customer"
    ) var loans: List<Loan> = listOf(),

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
) {
    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass = this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as Customer

        return id != null && id == other.id
    }

    final override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }
}
