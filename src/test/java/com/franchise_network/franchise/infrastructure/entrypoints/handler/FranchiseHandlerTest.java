package com.franchise_network.franchise.infrastructure.entrypoints.handler;

import com.franchise_network.franchise.domain.api.IFranchiseServicePort;
import com.franchise_network.franchise.domain.enums.TechnicalMessage;
import com.franchise_network.franchise.domain.exceptions.BusinessException;
import com.franchise_network.franchise.domain.exceptions.TechnicalException;
import com.franchise_network.franchise.domain.model.Franchise;
import com.franchise_network.franchise.infrastructure.entrypoints.dto.FranchiseDTO;
import com.franchise_network.franchise.infrastructure.entrypoints.dto.UpdateFranchiseNameDTO;
import com.franchise_network.franchise.infrastructure.entrypoints.mapper.IFranchiseMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class FranchiseHandlerTest {

    @Mock
    private IFranchiseServicePort franchiseServicePort;

    @Mock
    private IFranchiseMapper franchiseMapper;

    @InjectMocks
    private FranchiseHandler handler;

    @Test
    void createFranchise_success() {
        ServerRequest request = mock(ServerRequest.class);
        FranchiseDTO dto = new FranchiseDTO( "My Franchise");
        Franchise franchise = new Franchise(1L, "My Franchise");

        when(request.bodyToMono(FranchiseDTO.class)).thenReturn(Mono.just(dto));
        when(franchiseMapper.franchiseDTOToFranchise(dto)).thenReturn(franchise);
        when(franchiseServicePort.registerFranchise(franchise)).thenReturn(Mono.just(franchise));

        StepVerifier.create(handler.createFranchise(request))
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.CREATED))
                .verifyComplete();
    }

    @Test
    void createFranchise_businessException() {
        ServerRequest request = mock(ServerRequest.class);
        FranchiseDTO dto = new FranchiseDTO( "Dup");

        when(request.bodyToMono(FranchiseDTO.class)).thenReturn(Mono.just(dto));
        when(franchiseMapper.franchiseDTOToFranchise(dto)).thenReturn(new Franchise(1L, "Dup"));
        when(franchiseServicePort.registerFranchise(any()))
                .thenReturn(Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_ALREADY_EXISTS)));

        StepVerifier.create(handler.createFranchise(request))
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.BAD_REQUEST))
                .verifyComplete();
    }

    @Test
    void createFranchise_technicalException() {
        ServerRequest request = mock(ServerRequest.class);
        FranchiseDTO dto = new FranchiseDTO( "TechFail");

        when(request.bodyToMono(FranchiseDTO.class)).thenReturn(Mono.just(dto));
        when(franchiseMapper.franchiseDTOToFranchise(dto)).thenReturn(new Franchise(1L, "TechFail"));
        when(franchiseServicePort.registerFranchise(any()))
                .thenReturn(Mono.error(new TechnicalException(TechnicalMessage.INTERNAL_ERROR)));

        StepVerifier.create(handler.createFranchise(request))
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR))
                .verifyComplete();
    }

    @Test
    void createFranchise_unexpectedException() {
        ServerRequest request = mock(ServerRequest.class);
        FranchiseDTO dto = new FranchiseDTO( "Boom");

        when(request.bodyToMono(FranchiseDTO.class)).thenReturn(Mono.just(dto));
        when(franchiseMapper.franchiseDTOToFranchise(dto)).thenReturn(new Franchise(1L, "Boom"));
        when(franchiseServicePort.registerFranchise(any()))
                .thenReturn(Mono.error(new RuntimeException("Unexpected")));

        StepVerifier.create(handler.createFranchise(request))
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR))
                .verifyComplete();
    }
    @Test
    void updateFranchiseName_success() {
        ServerRequest request = mock(ServerRequest.class);
        Long franchiseId = 1L;
        String newName = "New Franchise Name";

        when(request.pathVariable("franchiseId")).thenReturn(franchiseId.toString());
        when(request.bodyToMono(UpdateFranchiseNameDTO.class)).thenReturn(Mono.just(new UpdateFranchiseNameDTO(newName)));
        when(franchiseServicePort.updateFranchiseName(franchiseId, newName))
                .thenReturn(Mono.just(new Franchise(franchiseId, newName)));

        StepVerifier.create(handler.updateFranchiseName(request))
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                .verifyComplete();

        verify(franchiseServicePort).updateFranchiseName(franchiseId, newName);
    }

    @Test
    void updateFranchiseName_businessException() {
        ServerRequest request = mock(ServerRequest.class);
        Long franchiseId = 1L;
        String newName = "Duplicate Name";

        when(request.pathVariable("franchiseId")).thenReturn(franchiseId.toString());
        when(request.bodyToMono(UpdateFranchiseNameDTO.class)).thenReturn(Mono.just(new UpdateFranchiseNameDTO(newName)));
        when(franchiseServicePort.updateFranchiseName(franchiseId, newName))
                .thenReturn(Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_ALREADY_EXISTS)));

        StepVerifier.create(handler.updateFranchiseName(request))
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.BAD_REQUEST))
                .verifyComplete();

        verify(franchiseServicePort).updateFranchiseName(franchiseId, newName);
    }

    @Test
    void updateFranchiseName_technicalException() {
        ServerRequest request = mock(ServerRequest.class);
        Long franchiseId = 1L;
        String newName = "TechError";

        when(request.pathVariable("franchiseId")).thenReturn(franchiseId.toString());
        when(request.bodyToMono(UpdateFranchiseNameDTO.class)).thenReturn(Mono.just(new UpdateFranchiseNameDTO(newName)));
        when(franchiseServicePort.updateFranchiseName(franchiseId, newName))
                .thenReturn(Mono.error(new TechnicalException(TechnicalMessage.INTERNAL_ERROR)));

        StepVerifier.create(handler.updateFranchiseName(request))
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR))
                .verifyComplete();

        verify(franchiseServicePort).updateFranchiseName(franchiseId, newName);
    }

    @Test
    void updateFranchiseName_unexpectedException() {
        ServerRequest request = mock(ServerRequest.class);
        Long franchiseId = 1L;
        String newName = "Boom";

        when(request.pathVariable("franchiseId")).thenReturn(franchiseId.toString());
        when(request.bodyToMono(UpdateFranchiseNameDTO.class)).thenReturn(Mono.just(new UpdateFranchiseNameDTO(newName)));
        when(franchiseServicePort.updateFranchiseName(franchiseId, newName))
                .thenReturn(Mono.error(new RuntimeException("Unexpected error")));

        StepVerifier.create(handler.updateFranchiseName(request))
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR))
                .verifyComplete();

        verify(franchiseServicePort).updateFranchiseName(franchiseId, newName);
    }




}
