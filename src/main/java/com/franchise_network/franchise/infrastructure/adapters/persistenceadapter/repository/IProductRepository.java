package com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.repository;

import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.entity.ProductEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface IProductRepository extends ReactiveCrudRepository<ProductEntity, Long> {
}

