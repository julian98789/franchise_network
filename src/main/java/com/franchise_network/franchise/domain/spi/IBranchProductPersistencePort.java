package com.franchise_network.franchise.domain.spi;

import com.franchise_network.franchise.domain.model.BranchProduct;
import reactor.core.publisher.Mono;

public interface IBranchProductPersistencePort {
    Mono<BranchProduct> save(BranchProduct branchProduct);
    Mono<Boolean> existsByBranchIdAndProductId(Long branchId, Long productId);
    Mono<Void> deleteByBranchIdAndProductId(Long branchId, Long productId);
    Mono<BranchProduct> findByBranchIdAndProductId(Long branchId, Long productId);
    Mono<BranchProduct> updateStock(Long branchId, Long productId, Integer newStock);


}