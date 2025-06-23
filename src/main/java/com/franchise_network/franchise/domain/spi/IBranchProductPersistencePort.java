package com.franchise_network.franchise.domain.spi;

import com.franchise_network.franchise.domain.model.BranchProduct;
import reactor.core.publisher.Mono;

public interface IBranchProductPersistencePort {
    Mono<BranchProduct> save(BranchProduct branchProduct);
    Mono<Boolean> existsByBranchIdAndProductId(Long branchId, Long productId);
}