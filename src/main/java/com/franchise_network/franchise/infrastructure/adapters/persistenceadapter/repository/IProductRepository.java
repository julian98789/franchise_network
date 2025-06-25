package com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.repository;

import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.entity.ProductEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface IProductRepository extends ReactiveCrudRepository<ProductEntity, Long> {
    Mono<Boolean> existsByName(String name);

}

