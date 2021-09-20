package br.com.zup.edu.cadastracarro

import javax.persistence.*

@Entity
class Proprietario(
    val nome: String,
    @field:Column(length = 11) val cpf: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}