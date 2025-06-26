package com.franchise_network.franchise.domain.usecase;

import com.franchise_network.franchise.domain.enums.TechnicalMessage;
import com.franchise_network.franchise.domain.exceptions.BusinessException;
import com.franchise_network.franchise.domain.model.Franchise;
import com.franchise_network.franchise.domain.spi.IFranchisePersistencePort;
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

        Franchise franchise = new Franchise(1L, "Franchise X");

        when(persistencePort.existsByName("Franchise X")).thenReturn(Mono.just(false));

        when(persistencePort.save(franchise)).thenReturn(Mono.just(franchise));

        StepVerifier.create(useCase.registerFranchise(franchise))
                .expectNext(franchise)
                .verifyComplete();


        verify(persistencePort).existsByName("Franchise X");

        verify(persistencePort).save(franchise);
    }



    @Test
    void updateFranchiseName_success() {
        Long id = 1L;

        String newName = "new Franchise";

        when(persistencePort.existsByName(newName)).thenReturn(Mono.just(false));
        when(persistencePort.existsById(id)).thenReturn(Mono.just(true));
        when(persistencePort.save(new Franchise(id, newName))).thenReturn(Mono.just(new Franchise(id, newName)));

        StepVerifier.create(useCase.updateFranchiseName(id, newName))
                .expectNextMatches(franchise -> franchise.id().equals(id) && franchise.name().equals(newName))
                .verifyComplete();

        verify(persistencePort).existsByName(newName);
        verify(persistencePort).existsById(id);
        verify(persistencePort).save(new Franchise(id, newName));
    }



    @Test
    void updateFranchiseName_franchiseNotFound_shouldThrowError() {
        Long id = 99L;

        String name = "New";


        when(persistencePort.existsByName(name)).thenReturn(Mono.just(false));
        when(persistencePort.existsById(id)).thenReturn(Mono.just(false));

        StepVerifier.create(useCase.updateFranchiseName(id, name))
                .expectErrorMatches(err ->
                        err instanceof BusinessException &&
                                err.getMessage().equals(TechnicalMessage.FRANCHISE_NOT_FOUND.getMessage()))
                .verify();

        verify(persistencePort).existsByName(name);
        verify(persistencePort).existsById(id);
        verify(persistencePort, never()).save(any());
    }
}
