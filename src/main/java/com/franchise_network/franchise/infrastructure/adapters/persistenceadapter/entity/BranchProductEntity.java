package com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.entity;

import lombok.*;
import org.springframework.data.relational.core.mapping.Table;

@Table("branch_product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BranchProductEntity {

    private Long branchId;
    private Long productId;
    private Integer stock;
}