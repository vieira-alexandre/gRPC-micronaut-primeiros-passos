package br.com.zup.edu

import io.grpc.ManagedChannelBuilder
import org.slf4j.LoggerFactory
import java.time.LocalDate
import kotlin.math.log

fun main() {
    val log = LoggerFactory.getLogger("Client")

    val channel = ManagedChannelBuilder
        .forAddress("localhost", 50051)
        .usePlaintext()
        .build()

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

    val client = FuncionarioServiceGrpc.newBlockingStub(channel)
    val response = client.cadastrar(request)

    log.info(response.toString())
}