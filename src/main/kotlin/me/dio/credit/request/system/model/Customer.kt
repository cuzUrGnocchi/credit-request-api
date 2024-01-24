package me.dio.credit.request.system.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotEmpty
import org.hibernate.proxy.HibernateProxy
import java.math.BigDecimal

@Entity
data class Customer(
    @Column(nullable = false)
    var firstName: String,

    @Column(nullable = false)
    var lastName: String,

    @Column(nullable = false, unique = true)
    val cpf: String,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false)
    var income: BigDecimal,

    @Column(nullable = false)
    var password: String,

    @Embedded
    @Column(nullable = false)
    val address: Address = Address(),

    @Column(nullable = false)
    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.REMOVE, CascadeType.PERSIST], mappedBy = "customer")
    val loans: MutableList<Loan> = mutableListOf(),

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) {
    constructor(id: Long): this("", "", "", "", BigDecimal.ZERO, "", Address(), mutableListOf(), id)
}
