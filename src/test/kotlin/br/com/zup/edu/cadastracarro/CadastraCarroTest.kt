package br.com.zup.edu.cadastracarro

import br.com.zup.edu.carros.CarroRequest
import br.com.zup.edu.carros.CarroServiceGrpc
import br.com.zup.edu.carros.Combustivel
import br.com.zup.edu.carros.Proprietario
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@MicronautTest(transactional = false)
internal class CadastraCarroTest {
    @Inject
    lateinit var repository: CarroRepository

    @Inject
    lateinit var client: CarroServiceGrpc.CarroServiceBlockingStub
    lateinit var carroRequest: CarroRequest
    lateinit var proprietario: Proprietario

    @BeforeEach
    internal fun setUp() {
        proprietario = Proprietario.newBuilder()
            .setNome("Alexandre")
            .setCpf("123.456.789-00")
            .build()

        carroRequest = CarroRequest.newBuilder()
            .setAno(2014)
            .setCombustivel(Combustivel.FLEX)
            .setModelo("Civic")
            .setPlaca("OQS1730")
            .setProprietario(proprietario)
            .build()
    }

    @Test
    @DisplayName("Deve cadastrar um carro")
    internal fun deveCadastrarUmCarroTest() {
        val response = client.cadastra(carroRequest)

        with(response) {
            assertNotNull(id)
            assertTrue(repository.existsById(id))
        }
    }

    @Test
    @DisplayName("Nao deve permitir cadastrar carro com placa repetida")
    internal fun naoDeveCadastrarCarroComPlacaRepetidaTest() {
        assertTrue(repository.count() == 1L)

        val error = assertThrows<StatusRuntimeException> {
            client.cadastra(carroRequest)
        }

        with(error) {
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals(1L, repository.count())
        }
    }

    @Test
    @DisplayName("Nao deve salvar carro com dados invalidos")
    fun naoDevePermitirComDadosInvalidos() {
        val count: Long = repository.count()

        val error = assertThrows<StatusRuntimeException> {
            val request = CarroRequest.newBuilder()
                .setAno(2014)
                .setModelo("Civic")
                .setPlaca("HIH6HH5")
                .setProprietario(proprietario)
                .build()

            client.cadastra(request)
        }

        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals(count, repository.count())
        }
    }

    @Factory
    class Clients {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): CarroServiceGrpc.CarroServiceBlockingStub? {
            return CarroServiceGrpc.newBlockingStub(channel)
        }
    }
}