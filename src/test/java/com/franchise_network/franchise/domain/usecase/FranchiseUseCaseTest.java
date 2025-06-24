package com.franchise_network.franchise.domain.usecase;

import com.franchise_network.franchise.domain.enums.TechnicalMessage;
import com.franchise_network.franchise.domain.exceptions.BusinessException;
import com.franchise_network.franchise.domain.model.Franchise;
import com.franchise_network.franchise.domain.spi.IFranchisePersistencePort;
import com.franchise_network.franchise.domain.usecase.FranchiseUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FranchiseUseCaseTest {

    @Mock
    private IFranchisePersistencePort persistencePort;

    @InjectMocks
    private FranchiseUseCase useCase;

    @Test
    void registerFranchise_success() {
        Franchise franchise = new Franchise(1L, "Franquicia X");

        when(persistencePort.existsByName("Franquicia X")).thenReturn(Mono.just(false));
        when(persistencePort.save(franchise)).thenReturn(Mono.just(franchise));

        StepVerifier.create(useCase.registerFranchise(franchise))
                .expectNext(franchise)
                .verifyComplete();

        verify(persistencePort).existsByName("Franquicia X");
        verify(persistencePort).save(franchise);
    }

    @Test
    void registerFranchise_invalidName_shouldThrowError() {
        Franchise franchise = new Franchise(1L, " ");

        StepVerifier.create(useCase.registerFranchise(franchise))
                .expectErrorMatches(err ->
                        err instanceof BusinessException &&
                                err.getMessage().equals(TechnicalMessage.INVALID_FRANCHISE_NAME.getMessage()))
                .verify();

        verifyNoInteractions(persistencePort);
    }

    @Test
    void registerFranchise_nameTooLong_shouldThrowError() {
        String longName = "F".repeat(101);
        Franchise franchise = new Franchise(1L, longName);

        StepVerifier.create(useCase.registerFranchise(franchise))
                .expectErrorMatches(err ->
                        err instanceof BusinessException &&
                                err.getMessage().equals(TechnicalMessage.INVALID_FRANCHISE_NAME.getMessage()))
                .verify();

        verifyNoInteractions(persistencePort);
    }

    @Test
    void registerFranchise_alreadyExists_shouldThrowError() {
        Franchise franchise = new Franchise(1L, "Franquicia Y");

        when(persistencePort.existsByName("Franquicia Y")).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.registerFranchise(franchise))
                .expectErrorMatches(err ->
                        err instanceof BusinessException &&
                                err.getMessage().equals(TechnicalMessage.FRANCHISE_ALREADY_EXISTS.getMessage()))
                .verify();

        verify(persistencePort).existsByName("Franquicia Y");
        verify(persistencePort, never()).save(any());
    }
}
