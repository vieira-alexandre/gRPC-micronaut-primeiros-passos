package br.com.zup.edu

import grpc.health.v1.HealthCheck
import grpc.health.v1.HealthGrpc
import io.grpc.stub.StreamObserver
import jakarta.inject.Singleton

@Singleton
class HealthCheckService : HealthGrpc.HealthImplBase(){
    override fun check(
        request: HealthCheck.HealthCheckRequest?,
        responseObserver: StreamObserver<HealthCheck.HealthCheckResponse>?
    ) {
        responseObserver?.onNext(HealthCheck.HealthCheckResponse.newBuilder()
            .setStatus(HealthCheck.HealthCheckResponse.ServingStatus.SERVING)
            .build())
        responseObserver?.onCompleted()
    }

    override fun watch(
        request: HealthCheck.HealthCheckRequest?,
        responseObserver: StreamObserver<HealthCheck.HealthCheckResponse>?
    ) {
        responseObserver?.onNext(HealthCheck.HealthCheckResponse.newBuilder()
            .setStatus(HealthCheck.HealthCheckResponse.ServingStatus.SERVING)
            .build())
    }
}