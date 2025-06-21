package com.franchise_network.franchise.domain.spi;

import com.franchise_network.franchise.domain.model.Branch;
import reactor.core.publisher.Mono;

public interface IBranchPersistencePort {
    Mono<Branch> save(Branch branch);

}