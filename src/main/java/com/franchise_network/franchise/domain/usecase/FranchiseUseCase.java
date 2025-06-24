package com.franchise_network.franchise.domain.usecase;

import com.franchise_network.franchise.domain.api.IFranchiseServicePort;
import com.franchise_network.franchise.domain.enums.TechnicalMessage;
import com.franchise_network.franchise.domain.exceptions.BusinessException;
import com.franchise_network.franchise.domain.model.Franchise;
import com.franchise_network.franchise.domain.spi.IFranchisePersistencePort;
import reactor.core.publisher.Mono;

public class FranchiseUseCase implements IFranchiseServicePort {

    private final IFranchisePersistencePort persistencePort;

    public FranchiseUseCase(IFranchisePersistencePort persistencePort) {
        this.persistencePort = persistencePort;
    }

    @Override
    public Mono<Franchise> registerFranchise(Franchise franchise) {
        return validateFranchiseName(franchise.name())
                .then(checkFranchiseNameNotExists(franchise.name()))
                .then(persistencePort.save(franchise));
    }

    @Override
    public Mono<Franchise> updateFranchiseName(Long franchiseId, String newName) {
        return validateFranchiseId(franchiseId)
                .then(validateFranchiseName(newName))
                .then(checkFranchiseNameNotExists(newName))
                .then(persistencePort.existsById(franchiseId))
                .flatMap(found -> {
                    if (Boolean.FALSE.equals(found)) {
                        return Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_NOT_FOUND));
                    }
                    return persistencePort.save(new Franchise(franchiseId, newName));
                });
    }

    private Mono<Void> validateFranchiseId(Long id) {
        if (id == null) {
            return Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_ID_REQUIRED));
        }
        return Mono.empty();
    }

    private Mono<Void> validateFranchiseName(String name) {
        if (name == null || name.isBlank() || name.length() > 100) {
            return Mono.error(new BusinessException(TechnicalMessage.INVALID_FRANCHISE_NAME));
        }
        return Mono.empty();
    }

    private Mono<Void> checkFranchiseNameNotExists(String name) {
        return persistencePort.existsByName(name)
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_ALREADY_EXISTS));
                    }
                    return Mono.empty();
                });
    }
}
