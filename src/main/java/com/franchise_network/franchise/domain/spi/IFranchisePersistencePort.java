package com.franchise_network.franchise.domain.spi;

import com.franchise_network.franchise.domain.model.Franchise;
import reactor.core.publisher.Mono;

public interface IFranchisePersistencePort {
    Mono<Franchise> save(Franchise franchise);
    Mono<Boolean> existsByName(String name);
    Mono<Boolean>existsById(Long id);
}