package com.franchise_network.franchise.infrastructure.entrypoints.handler;

import org.springframework.web.reactive.function.server.ServerResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class HealthCheckHandlerTest {

    private HealthCheckHandler handler;

    @Mock
    private ServerRequest serverRequest;

    @BeforeEach
    void setUp() {
        handler = new HealthCheckHandler();
    }

    @Test
    void health_shouldReturnOkWithoutBody() {
        Mono<ServerResponse> responseMono = handler.health(serverRequest);

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }
}
