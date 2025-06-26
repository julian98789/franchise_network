package com.franchise_network.franchise.infrastructure.entrypoints.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class HealthCheckHandler {
    public Mono<ServerResponse> health(ServerRequest request) {
        return ServerResponse.ok().build();
    }
}