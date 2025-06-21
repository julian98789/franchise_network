package com.franchise_network.franchise.domain.usecase;

import com.franchise_network.franchise.domain.api.IBranchServicePort;
import com.franchise_network.franchise.domain.enums.TechnicalMessage;
import com.franchise_network.franchise.domain.exceptions.BusinessException;
import com.franchise_network.franchise.domain.model.Branch;
import com.franchise_network.franchise.domain.spi.IBranchPersistencePort;
import com.franchise_network.franchise.domain.spi.IFranchisePersistencePort;
import reactor.core.publisher.Mono;

public class BranchUseCase implements IBranchServicePort {

    private final IBranchPersistencePort persistencePort;
    private final IFranchisePersistencePort franchisePersistencePort;

    public BranchUseCase(IBranchPersistencePort persistencePort, IFranchisePersistencePort franchisePersistencePort) {
        this.persistencePort = persistencePort;
        this.franchisePersistencePort = franchisePersistencePort;
    }

    @Override
    public Mono<Branch> addBranchToFranchise(Branch branch) {
        if (branch.name() == null || branch.name().isBlank() || branch.name().length() > 100) {
            return Mono.error(new BusinessException(TechnicalMessage.INVALID_BRANCH_NAME));
        }

        return franchisePersistencePort.existsById(branch.franchiseId())
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_NOT_FOUND));
                    }
                    return persistencePort.save(branch);
                });
    }
}
