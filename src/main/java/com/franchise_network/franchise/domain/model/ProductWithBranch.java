package com.franchise_network.franchise.domain.model;

public record ProductWithBranch(
        Long branchId,
        String branchName,
        Long productId,
        String productName,
        Integer stock
) {}