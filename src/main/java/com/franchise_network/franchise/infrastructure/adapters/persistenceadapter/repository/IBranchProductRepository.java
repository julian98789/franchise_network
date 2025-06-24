package com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.repository;

import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.entity.BranchProductEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface IBranchProductRepository extends R2dbcRepository<BranchProductEntity, Void> {
    Mono<Boolean> existsByBranchIdAndProductId(Long branchId, Long productId);
    Mono<Void> deleteByBranchIdAndProductId(Long branchId, Long productId);
    Mono<BranchProductEntity> findByBranchIdAndProductId(Long branchId, Long productId);

    @Query("UPDATE branch_product SET stock = :stock WHERE branch_id = :branchId AND product_id = :productId")
    Mono<Integer> updateStock(Long branchId, Long productId, Integer stock);

    Flux<BranchProductEntity> findByBranchId(Long branchId);


}