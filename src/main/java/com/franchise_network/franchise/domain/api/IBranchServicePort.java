package com.franchise_network.franchise.domain.api;

import com.franchise_network.franchise.domain.model.Branch;
import reactor.core.publisher.Mono;

public interface IBranchServicePort {
    Mono<Branch> addBranchToFranchise(Branch branch);
    Mono<Branch> updateBranchName(Long branchId, String newName);

}