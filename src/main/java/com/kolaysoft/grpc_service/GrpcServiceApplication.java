package com.kolaysoft.grpc_service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.grpc.Server;
import io.grpc.ServerBuilder;

@SpringBootApplication
public class GrpcServiceApplication {

	private Server server;

	// Read the gRPC port from application.properties
	@Value("${grpc.server.port}")
	private int grpcPort;

	public static void main(String[] args) {
		SpringApplication.run(GrpcServiceApplication.class, args);
	}

	// Bean to start gRPC Server
	@PostConstruct
	public void startGrpcServer() throws Exception {
		server = ServerBuilder.forPort(grpcPort) // Use port from application.properties
				.addService(new FileServiceImpl()) // Add your gRPC service
				.build()
				.start();
		System.out.println("gRPC Server started on port " + grpcPort);
		server.awaitTermination(); // Prevents the server from exiting immediately
	}

	// Shutdown hook to gracefully stop the gRPC server
	@PreDestroy
	public void stopGrpcServer() throws InterruptedException {
		if (server != null) {
			server.shutdown();
			System.out.println("gRPC Server stopped");
		}
	}
}
