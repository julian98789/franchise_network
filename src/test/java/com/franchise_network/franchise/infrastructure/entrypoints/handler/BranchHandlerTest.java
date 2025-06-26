package com.franchise_network.franchise.infrastructure.entrypoints.handler;

import com.franchise_network.franchise.domain.api.IBranchServicePort;
import com.franchise_network.franchise.domain.enums.TechnicalMessage;
import com.franchise_network.franchise.domain.exceptions.BusinessException;
import com.franchise_network.franchise.domain.exceptions.TechnicalException;
import com.franchise_network.franchise.domain.model.Branch;
import com.franchise_network.franchise.infrastructure.entrypoints.dto.BranchDTO;
import com.franchise_network.franchise.infrastructure.entrypoints.dto.UpdateBranchNameDTO;
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
        BranchDTO dto = new BranchDTO("Branch A", 1L);
        Branch branch = new Branch(null, "Branch A", 1L);

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
        BranchDTO dto = new BranchDTO("Branch B", 1L);
        Branch branch = new Branch(null, "Branch B", 1L);

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
        BranchDTO dto = new BranchDTO("Branch C", 1L);
        Branch branch = new Branch(null, "Branch C", 1L);

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
        BranchDTO dto = new BranchDTO("Branch D", 1L);
        Branch branch = new Branch(null, "Branch D", 1L);

        when(request.bodyToMono(BranchDTO.class)).thenReturn(Mono.just(dto));
        when(branchMapper.branchDTOToBranch(dto)).thenReturn(branch);
        when(branchServicePort.addBranchToFranchise(branch))
                .thenReturn(Mono.error(new RuntimeException("Unexpected error")));

        Mono<ServerResponse> responseMono = handler.addBranchToFranchise(request);

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR))
                .verifyComplete();
    }

    @Test
    void updateBranchName_success() {
        ServerRequest request = mock(ServerRequest.class);
        Long branchId = 123L;
        UpdateBranchNameDTO dto = new UpdateBranchNameDTO("Updated Branch");
        Branch updated = new Branch(branchId, "Updated Branch", 1L);

        when(request.pathVariable("branchId")).thenReturn(branchId.toString());
        when(request.bodyToMono(UpdateBranchNameDTO.class)).thenReturn(Mono.just(dto));
        when(branchServicePort.updateBranchName(branchId, dto.getName())).thenReturn(Mono.just(updated));

        Mono<ServerResponse> response = handler.updateBranchName(request);

        StepVerifier.create(response)
                .expectNextMatches(res -> res.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void updateBranchName_businessException() {
        ServerRequest request = mock(ServerRequest.class);
        Long branchId = 456L;
        UpdateBranchNameDTO dto = new UpdateBranchNameDTO("Duplicate Name");

        when(request.pathVariable("branchId")).thenReturn(branchId.toString());
        when(request.bodyToMono(UpdateBranchNameDTO.class)).thenReturn(Mono.just(dto));
        when(branchServicePort.updateBranchName(branchId, dto.getName()))
                .thenReturn(Mono.error(new BusinessException(TechnicalMessage.BRANCH_NAME_ALREADY_EXISTS)));

        Mono<ServerResponse> response = handler.updateBranchName(request);

        StepVerifier.create(response)
                .expectNextMatches(res -> res.statusCode().equals(HttpStatus.BAD_REQUEST))
                .verifyComplete();
    }

    @Test
    void updateBranchName_technicalException() {
        ServerRequest request = mock(ServerRequest.class);
        Long branchId = 789L;
        UpdateBranchNameDTO dto = new UpdateBranchNameDTO("Main Branch");

        when(request.pathVariable("branchId")).thenReturn(branchId.toString());
        when(request.bodyToMono(UpdateBranchNameDTO.class)).thenReturn(Mono.just(dto));
        when(branchServicePort.updateBranchName(branchId, dto.getName()))
                .thenReturn(Mono.error(new TechnicalException(TechnicalMessage.INTERNAL_ERROR)));

        Mono<ServerResponse> response = handler.updateBranchName(request);

        StepVerifier.create(response)
                .expectNextMatches(res -> res.statusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR))
                .verifyComplete();
    }

    @Test
    void updateBranchName_unexpectedException() {
        ServerRequest request = mock(ServerRequest.class);
        Long branchId = 999L;
        UpdateBranchNameDTO dto = new UpdateBranchNameDTO("Test Branch");

        when(request.pathVariable("branchId")).thenReturn(branchId.toString());
        when(request.bodyToMono(UpdateBranchNameDTO.class)).thenReturn(Mono.just(dto));
        when(branchServicePort.updateBranchName(branchId, dto.getName()))
                .thenReturn(Mono.error(new RuntimeException("Database connection failed")));

        Mono<ServerResponse> response = handler.updateBranchName(request);

        StepVerifier.create(response)
                .expectNextMatches(res -> res.statusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR))
                .verifyComplete();
    }
}