package br.com.zup.edu

import com.google.protobuf.Timestamp
import io.grpc.ServerBuilder
import io.grpc.stub.StreamObserver
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.ZoneId

//fun main() {
//    val server = ServerBuilder
//        .forPort(50051)
//        .addService(FuncionarioEndpoint())
//        .build()
//
//    server.start()
//    server.awaitTermination()
//}

@Singleton
class FuncionarioEndpoint : FuncionarioServiceGrpc.FuncionarioServiceImplBase() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun cadastrar(request: FuncionarioRequest?, responseObserver: StreamObserver<FuncionarioResponse>?) {

        log.info(request!!.toString())

        var nome: String? = request.nome

        if (!request.hasField(FuncionarioRequest.getDescriptor().findFieldByName("nome"))) {
            nome = "[???]"
        }

        val instant = LocalDateTime.now().atZone(ZoneId.of("UTC")).toInstant()
        val criadoEm = Timestamp.newBuilder()
            .setSeconds(instant.epochSecond)
            .setNanos(instant.nano)
            .build()

        val response = FuncionarioResponse.newBuilder()
            .setNome(nome)
            .setCriadoEm(criadoEm)
            .build()

        responseObserver?.onNext(response)
        responseObserver?.onCompleted()
    }
}