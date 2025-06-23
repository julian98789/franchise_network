package com.franchise_network.franchise.infrastructure.entrypoints.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BranchProductDTO {
    private Long branchId;
    private Long productId;
    private Integer stock;
}