package com.apssouza.grpc;

import com.apssouza.grpc.interceptors.AggregateServerInterceptor;
import com.apssouza.grpc.interceptors.CancelledRequestInterceptor;
import com.apssouza.grpc.interceptors.StopwatchServerInterceptor;
import com.apssouza.grpc.interceptors.UnexpectedExceptionInterceptor;
import com.apssouza.grpc.server.GrpcServer;
import com.apssouza.grpc.server.GrpcServerBuilder;
import com.apssouza.grpc.server.HealthCheckService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.grpc.BindableService;
import io.grpc.ServerInterceptor;


public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        List<BindableService> services = new ArrayList<>();
        services.add(new HealthCheckService());
        GrpcServerBuilder builder = GrpcServerBuilder.port(50051);
        GrpcServer grpcServer = builder.services(services)
                .fixedThreadPool(4)
                .maxConnectionAge(5, TimeUnit.MINUTES)
                .interceptors(getInterceptors())
                .build();
        grpcServer.start();
    }

    private static List<ServerInterceptor> getInterceptors() {
        ServerInterceptor aggregate = new AggregateServerInterceptor(
                new CancelledRequestInterceptor(),
                new StopwatchServerInterceptor(),
                new UnexpectedExceptionInterceptor()
        );
        return Arrays.asList(aggregate);
    }
}