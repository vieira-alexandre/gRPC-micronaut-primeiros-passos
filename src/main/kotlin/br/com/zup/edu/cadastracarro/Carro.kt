package br.com.zup.edu.cadastracarro

import javax.persistence.*

@Entity
class Carro(
    val modelo: String,
    @field:Column(length = 7, nullable = false) val placa: String,
    @field:Enumerated(EnumType.STRING) val combustivel: Combustivel,
    @field:ManyToOne(cascade = [CascadeType.ALL]) val proprietario: Proprietario
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}