package br.com.zup.edu.cadastracarro

import br.com.zup.edu.carros.CarroRequest
import br.com.zup.edu.carros.CarroResponse
import br.com.zup.edu.carros.CarroServiceGrpc
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.stub.StreamObserver
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.ZoneId
import javax.transaction.Transactional

@Singleton
class CadastraCarroEndpoint : CarroServiceGrpc.CarroServiceImplBase() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var repository: CarroRepository

    @Transactional
    override fun cadastra(request: CarroRequest?, responseObserver: StreamObserver<CarroResponse>?) {
        log.info(request!!.toString())

        if(repository.existsByPlaca(request.placa)) {
            responseObserver?.onError(StatusRuntimeException(Status.ALREADY_EXISTS))
            return
        }

        if(!request.placa.matches("[A-Z]{3}[0-9][0-9A-Z][0-9]{2}".toRegex())) {
            responseObserver?.onError(StatusRuntimeException(Status.INVALID_ARGUMENT))
            return
        }

        val carro = request.toModel()
        repository.save(carro)

        val now = Instant.now().atZone(ZoneId.of("UTC")).toInstant()
        val criadoEm = com.google.protobuf.Timestamp.newBuilder()
            .setSeconds(now.epochSecond)
            .setNanos(now.nano)

        val response = CarroResponse.newBuilder()
            .setCriadoEm(criadoEm)
            .setId(carro.id!!)
            .build()

        responseObserver?.onNext(response)
        responseObserver?.onCompleted()
    }
}

fun CarroRequest.toModel(): Carro {
    return Carro(
        modelo = modelo,
        placa = placa,
        combustivel = Combustivel.obterCombustivelPeloCodigo(combustivelValue),
        proprietario = proprietario.toModel()
    )
}

fun br.com.zup.edu.carros.Proprietario.toModel(): Proprietario {
    var cpfSomenteNumeros: String = cpf.replace(".", "", true)
    cpfSomenteNumeros = cpfSomenteNumeros.replace("-", "", true)
    return Proprietario(
        nome = nome,
        cpf = cpfSomenteNumeros
    )
}
