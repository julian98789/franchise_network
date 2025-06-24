package com.franchise_network.franchise.domain.usecase;


import com.franchise_network.franchise.domain.enums.TechnicalMessage;
import com.franchise_network.franchise.domain.exceptions.BusinessException;
import com.franchise_network.franchise.domain.model.Branch;
import com.franchise_network.franchise.domain.spi.IBranchPersistencePort;
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
class BranchUseCaseTest {

    @Mock
    private IBranchPersistencePort branchPersistencePort;

    @Mock
    private IFranchisePersistencePort franchisePersistencePort;

    @InjectMocks
    private BranchUseCase useCase;

    @Test
    void addBranchToFranchise_success() {
        Branch branch = new Branch(1L, "Sucursal A", 10L);

        when(branchPersistencePort.existsByNameAndFranchiseId("Sucursal A", 10L)).thenReturn(Mono.just(false));
        when(franchisePersistencePort.existsById(10L)).thenReturn(Mono.just(true));
        when(branchPersistencePort.save(branch)).thenReturn(Mono.just(branch));

        StepVerifier.create(useCase.addBranchToFranchise(branch))
                .expectNext(branch)
                .verifyComplete();
    }

    @Test
    void addBranchToFranchise_invalidName_shouldThrowError() {
        Branch branch = new Branch(1L, "", 10L);

        StepVerifier.create(useCase.addBranchToFranchise(branch))
                .expectErrorMatches(err -> err instanceof BusinessException &&
                        err.getMessage().equals(TechnicalMessage.INVALID_BRANCH_NAME.getMessage()))
                .verify();
    }

    @Test
    void addBranchToFranchise_nullFranchiseId_shouldThrowError() {
        Branch branch = new Branch(1L, "Sucursal A", null);

        StepVerifier.create(useCase.addBranchToFranchise(branch))
                .expectErrorMatches(err -> err instanceof BusinessException &&
                        err.getMessage().equals(TechnicalMessage.FRANCHISE_ID_REQUIRED.getMessage()))
                .verify();
    }

    @Test
    void addBranchToFranchise_branchNameAlreadyExists_shouldThrowError() {
        Branch branch = new Branch(1L, "Sucursal A", 10L);

        when(branchPersistencePort.existsByNameAndFranchiseId("Sucursal A", 10L)).thenReturn(Mono.just(true));
        when(franchisePersistencePort.existsById(10L)).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.addBranchToFranchise(branch))
                .expectErrorMatches(err -> err instanceof BusinessException &&
                        err.getMessage().equals(TechnicalMessage.BRANCH_NAME_ALREADY_EXISTS.getMessage()))
                .verify();
    }

    @Test
    void addBranchToFranchise_franchiseNotFound_shouldThrowError() {
        Branch branch = new Branch(1L, "Sucursal A", 10L);

        when(branchPersistencePort.existsByNameAndFranchiseId("Sucursal A", 10L)).thenReturn(Mono.just(false));
        when(franchisePersistencePort.existsById(10L)).thenReturn(Mono.just(false));

        StepVerifier.create(useCase.addBranchToFranchise(branch))
                .expectErrorMatches(err -> err instanceof BusinessException &&
                        err.getMessage().equals(TechnicalMessage.FRANCHISE_NOT_FOUND.getMessage()))
                .verify();
    }

    @Test
    void updateBranchName_success() {
        Long branchId = 1L;
        String newName = "Sucursal Actualizada";
        Branch existing = new Branch(branchId, "Sucursal A", 10L);
        Branch updated = new Branch(branchId, newName, 10L);

        when(branchPersistencePort.findById(branchId)).thenReturn(Mono.just(existing));
        when(branchPersistencePort.existsByNameAndFranchiseId(newName, 10L)).thenReturn(Mono.just(false));
        when(branchPersistencePort.save(updated)).thenReturn(Mono.just(updated));

        StepVerifier.create(useCase.updateBranchName(branchId, newName))
                .expectNext(updated)
                .verifyComplete();
    }

    @Test
    void updateBranchName_nullBranchId_shouldThrowError() {
        when(branchPersistencePort.findById(null)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.updateBranchName(null, "Nuevo Nombre"))
                .expectErrorMatches(err -> err instanceof BusinessException &&
                        err.getMessage().equals(TechnicalMessage.BRANCH_ID_REQUIRED.getMessage()))
                .verify();
    }

    @Test
    void updateBranchName_invalidName_shouldThrowError() {
        when(branchPersistencePort.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.updateBranchName(1L, ""))
                .expectErrorMatches(err -> err instanceof BusinessException &&
                        err.getMessage().equals(TechnicalMessage.INVALID_BRANCH_NAME.getMessage()))
                .verify();
    }

    @Test
    void updateBranchName_branchNotFound_shouldThrowError() {
        when(branchPersistencePort.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.updateBranchName(1L, "Nuevo Nombre"))
                .expectErrorMatches(err -> err instanceof BusinessException &&
                        err.getMessage().equals(TechnicalMessage.BRANCH_NOT_FOUND.getMessage()))
                .verify();
    }



}
