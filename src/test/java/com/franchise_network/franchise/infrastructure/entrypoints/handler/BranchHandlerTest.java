package com.franchise_network.franchise.infrastructure.entrypoints.handler;


import com.franchise_network.franchise.domain.api.IBranchServicePort;
import com.franchise_network.franchise.domain.enums.TechnicalMessage;
import com.franchise_network.franchise.domain.exceptions.BusinessException;
import com.franchise_network.franchise.domain.exceptions.TechnicalException;
import com.franchise_network.franchise.domain.model.Branch;
import com.franchise_network.franchise.infrastructure.entrypoints.dto.BranchDTO;
import com.franchise_network.franchise.infrastructure.entrypoints.mapper.IBranchMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BranchHandlerTest {

    @Mock
    private IBranchServicePort branchServicePort;

    @Mock
    private IBranchMapper branchMapper;

    private BranchHandler handler;

    @BeforeEach
    void setUp() {
        handler = new BranchHandler(branchServicePort, branchMapper);
    }

    @Test
    void addBranchToFranchise_success() {
        ServerRequest request = mock(ServerRequest.class);
        BranchDTO dto = new BranchDTO("Sucursal A", 1L);
        Branch branch = new Branch(null, "Sucursal A", 1L);

        when(request.bodyToMono(BranchDTO.class)).thenReturn(Mono.just(dto));
        when(branchMapper.branchDTOToBranch(dto)).thenReturn(branch);
        when(branchServicePort.addBranchToFranchise(branch)).thenReturn(Mono.just(branch));

        Mono<ServerResponse> responseMono = handler.addBranchToFranchise(request);

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.CREATED))
                .verifyComplete();
    }

    @Test
    void addBranchToFranchise_businessException() {
        ServerRequest request = mock(ServerRequest.class);
        BranchDTO dto = new BranchDTO("Sucursal B", 1L);
        Branch branch = new Branch(null, "Sucursal B", 1L);

        when(request.bodyToMono(BranchDTO.class)).thenReturn(Mono.just(dto));
        when(branchMapper.branchDTOToBranch(dto)).thenReturn(branch);
        when(branchServicePort.addBranchToFranchise(branch))
                .thenReturn(Mono.error(new BusinessException(TechnicalMessage.BRANCH_NAME_ALREADY_EXISTS)));

        Mono<ServerResponse> responseMono = handler.addBranchToFranchise(request);

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.BAD_REQUEST))
                .verifyComplete();
    }

    @Test
    void addBranchToFranchise_technicalException() {
        ServerRequest request = mock(ServerRequest.class);
        BranchDTO dto = new BranchDTO("Sucursal C", 1L);
        Branch branch = new Branch(null, "Sucursal C", 1L);

        when(request.bodyToMono(BranchDTO.class)).thenReturn(Mono.just(dto));
        when(branchMapper.branchDTOToBranch(dto)).thenReturn(branch);
        when(branchServicePort.addBranchToFranchise(branch))
                .thenReturn(Mono.error(new TechnicalException(TechnicalMessage.INTERNAL_ERROR)));

        Mono<ServerResponse> responseMono = handler.addBranchToFranchise(request);

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR))
                .verifyComplete();
    }

    @Test
    void addBranchToFranchise_unexpectedException() {
        ServerRequest request = mock(ServerRequest.class);
        BranchDTO dto = new BranchDTO("Sucursal D", 1L);
        Branch branch = new Branch(null, "Sucursal D", 1L);

        when(request.bodyToMono(BranchDTO.class)).thenReturn(Mono.just(dto));
        when(branchMapper.branchDTOToBranch(dto)).thenReturn(branch);
        when(branchServicePort.addBranchToFranchise(branch))
                .thenReturn(Mono.error(new RuntimeException("Unexpected")));

        Mono<ServerResponse> responseMono = handler.addBranchToFranchise(request);

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR))
                .verifyComplete();
    }
}

