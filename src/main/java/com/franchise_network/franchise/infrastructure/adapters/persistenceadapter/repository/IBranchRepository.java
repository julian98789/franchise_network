package com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.repository;

import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.entity.BranchEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface IBranchRepository extends ReactiveCrudRepository<BranchEntity, Long> {
    Mono<Boolean> existsByNameAndFranchiseId(String name, Long franchiseId);

}