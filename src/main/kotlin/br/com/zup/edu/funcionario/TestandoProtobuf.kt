package br.com.zup.edu

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.FileInputStream
import java.io.FileOutputStream

fun main() {
    val log: Logger = LoggerFactory.getLogger("TestandoProtobuf.kt")
    val request = FuncionarioRequest.newBuilder()
        .setNome("Yuri Matheus")
        .setCpf("000.000.000-00")
        .setSalario(2000.20)
        .setAtivo(true)
        .setCargo(Cargo.QA)
        .addEnderecos(
            FuncionarioRequest.Endereco.newBuilder()
                .setLogradouro("Rua dos bobos")
                .setCep("12345-123")
                .setComplemento("numero 0")
                .build()
        )
        .build()

    println(request.toString())

    request.writeTo(FileOutputStream("funcionario-request.bin"))
    log.info("arquivo salvo")


    val carregadoDoDisco = FuncionarioRequest.newBuilder().mergeFrom(FileInputStream("funcionario-request.bin"))
        .setCargo(Cargo.DEV)
        .build()
    log.info("arquivo carregado e objeto alterado")
    println(carregadoDoDisco.toString())
    log.info("impresso com alteracoes")
}