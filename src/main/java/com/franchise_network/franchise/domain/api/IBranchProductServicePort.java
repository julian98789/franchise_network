package com.franchise_network.franchise.domain.api;

import com.franchise_network.franchise.domain.model.BranchProduct;
import com.franchise_network.franchise.domain.model.ProductWithBranch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IBranchProductServicePort {
    Mono<BranchProduct> assignProductToBranch(BranchProduct branchProduct);
    Mono<Void> removeProductFromBranch(Long branchId, Long productId);
    Mono<BranchProduct> updateStock(Long branchId, Long productId, Integer newStock);
    Flux<ProductWithBranch> getTopProductByStockPerBranch(Long franchiseId);


}
