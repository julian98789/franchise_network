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
        if (franchise.name() == null || franchise.name().isBlank() || franchise.name().length() > 100) {
            return Mono.error(new BusinessException(TechnicalMessage.INVALID_FRANCHISE_NAME));
        }

        return persistencePort.existsByName(franchise.name())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_ALREADY_EXISTS));
                    }
                    return persistencePort.save(franchise);
                });
    }
}
