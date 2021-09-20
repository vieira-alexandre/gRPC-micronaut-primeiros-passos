package br.com.zup.edu.serasa

import br.com.zup.edu.carros.SerasaServiceGrpc
import br.com.zup.edu.carros.SituacaoDoClienteRequest
import br.com.zup.edu.carros.SituacaoDoClienteResponse
import com.google.rpc.Code
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import io.grpc.stub.StreamObserver
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import kotlin.random.Random

@Singleton
class SerasaEndpoint : SerasaServiceGrpc.SerasaServiceImplBase() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun verificarSituacaoDoCliente(
        request: SituacaoDoClienteRequest?,
        responseObserver: StreamObserver<SituacaoDoClienteResponse>?
    ) {
        log.info("${request!!}")
        val cpf = request.cpf

        if (cpf.isNullOrBlank()) {
            val error = Status.INVALID_ARGUMENT
                .withDescription("cpf deve ser informado")
                .asRuntimeException()
            responseObserver?.onError(error)
            return
        }

        if (!cpf.matches("\\d{3}.?\\d{3}.?\\d{3}-?\\d{2}".toRegex())) {
            responseObserver?.onError(
                Status.INVALID_ARGUMENT
                    .withDescription("cpf inválido")
                    .augmentDescription("formato esperado é 111.111.111-11")
                    .asRuntimeException()
            )
            return
        }

        //simulando erro de segurança
        if (cpf.endsWith("1-11")) {
            val statusProto: com.google.rpc.Status = com.google.rpc.Status.newBuilder()
                .setCode(Code.PERMISSION_DENIED_VALUE)
                .setMessage("sem permissão para acessar este recurso")
                .build()
            responseObserver?.onError(StatusProto.toStatusRuntimeException(statusProto))
            return
        }

        try {
            val valor = Random.nextDouble(from = 0.0, until = 140.0)
            if (valor > 100) throw IllegalStateException("Erro inesperado simulado. Valor $valor")
        } catch (ex: IllegalStateException) {
//            responseObserver?.onError(
//                Status.INTERNAL
//                    .withDescription(ex.message)
//                    .withCause(ex)
//                    .asRuntimeException()
//            )

            val exception =  StatusRuntimeException(Status.INTERNAL.withDescription(ex.message).withCause(ex))
            responseObserver?.onError(exception)
            return
        }


        val result = when {
            cpfEstaRegular(request.cpf) -> 1
            else -> 2
        }

        val response = SituacaoDoClienteResponse.newBuilder()
            .setSituacaoValue(result)
            .build()

        responseObserver?.onNext(response)
        responseObserver?.onCompleted()
    }

    private fun cpfEstaRegular(cpf: String): Boolean = cpf.first().digitToInt() < 5

}
