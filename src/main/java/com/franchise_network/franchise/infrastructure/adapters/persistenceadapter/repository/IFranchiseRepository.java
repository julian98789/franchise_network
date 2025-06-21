package com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.repository;

import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.entity.FranchiseEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface IFranchiseRepository extends ReactiveCrudRepository<FranchiseEntity, Long> {
    Mono<Boolean> existsByName(String name);
}