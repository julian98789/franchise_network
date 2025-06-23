package com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.repository;

import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.entity.BranchProductEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface IBranchProductRepository extends R2dbcRepository<BranchProductEntity, Void> {
    Mono<Boolean> existsByBranchIdAndProductId(Long branchId, Long productId);

}