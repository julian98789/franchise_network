package com.franchise_network.franchise.domain.api;

import com.franchise_network.franchise.domain.model.BranchProduct;
import reactor.core.publisher.Mono;

public interface IBranchProductServicePort {
    Mono<BranchProduct> assignProductToBranch(BranchProduct branchProduct);
}
