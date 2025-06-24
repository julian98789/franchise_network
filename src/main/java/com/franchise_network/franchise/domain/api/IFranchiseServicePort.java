package com.franchise_network.franchise.domain.api;

import com.franchise_network.franchise.domain.model.Franchise;
import reactor.core.publisher.Mono;

public interface IFranchiseServicePort {
    Mono<Franchise> registerFranchise(Franchise franchise);
    Mono<Franchise> updateFranchiseName(Long franchiseId, String newName);

}