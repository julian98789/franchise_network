package com.franchise_network.franchise.domain.usecase;

import com.franchise_network.franchise.domain.api.IBranchServicePort;
import com.franchise_network.franchise.domain.constants.Constants;
import com.franchise_network.franchise.domain.enums.TechnicalMessage;
import com.franchise_network.franchise.domain.exceptions.BusinessException;
import com.franchise_network.franchise.domain.model.Branch;
import com.franchise_network.franchise.domain.spi.IBranchPersistencePort;
import com.franchise_network.franchise.domain.spi.IFranchisePersistencePort;
import reactor.core.publisher.Mono;

public class BranchUseCase implements IBranchServicePort {

    private final IBranchPersistencePort persistencePort;
    private final IFranchisePersistencePort franchisePersistencePort;

    public BranchUseCase(IBranchPersistencePort persistencePort,
                         IFranchisePersistencePort franchisePersistencePort) {
        this.persistencePort = persistencePort;
        this.franchisePersistencePort = franchisePersistencePort;
    }

    public Mono<Branch> addBranchToFranchise(Branch branch) {
        return validateBranchName(branch.name())
                .then(validateFranchiseId(branch.franchiseId()))
                .flatMap(validFranchiseId ->
                        checkBranchNameNotExists(branch.name(), validFranchiseId)
                                .then(franchisePersistencePort.existsById(validFranchiseId)
                                        .flatMap(exists -> {
                                            if (Boolean.FALSE.equals(exists)) {
                                                return Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_NOT_FOUND));
                                            }
                                            return persistencePort.save(branch);
                                        }))
                );
    }

    @Override
    public Mono<Branch> updateBranchName(Long branchId, String newName) {
        return validateBranchId(branchId)
                .then(validateBranchName(newName))
                .then(persistencePort.findById(branchId))
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.BRANCH_NOT_FOUND)))
                .flatMap(existingBranch ->
                        checkBranchNameNotExists(newName, existingBranch.franchiseId())
                                .then(persistencePort.save(
                                        new Branch(branchId, newName, existingBranch.franchiseId())
                                ))
                );
    }


    private Mono<Void> validateBranchId(Long id) {
        if (id == null) {
            return Mono.error(new BusinessException(TechnicalMessage.BRANCH_ID_REQUIRED));
        }
        return Mono.empty();
    }

    private Mono<Void> checkBranchNameNotExists(String name, Long franchiseId) {
        return persistencePort.existsByNameAndFranchiseId(name, franchiseId)
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new BusinessException(TechnicalMessage.BRANCH_NAME_ALREADY_EXISTS));
                    }
                    return Mono.empty();
                });
    }


    private Mono<Long> validateFranchiseId(Long franchiseId) {
        if (franchiseId == null) {
            return Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_ID_REQUIRED));
        }
        return Mono.just(franchiseId);
    }


    private Mono<Void> validateBranchName(String name) {
        if (name == null || name.isBlank() || name.length() > Constants.MAX_BRANCH_NAME_LENGTH) {
            return Mono.error(new BusinessException(TechnicalMessage.INVALID_BRANCH_NAME));
        }
        return Mono.empty();
    }

}
