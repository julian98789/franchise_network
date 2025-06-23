package com.franchise_network.franchise.domain.model;

public record BranchProduct(
        Long branchId,
        Long productId,
        Integer stock
) {}