package com.franchise_network.franchise.infrastructure.entrypoints.mapper;

import com.franchise_network.franchise.domain.model.BranchProduct;
import com.franchise_network.franchise.domain.model.ProductWithBranch;
import com.franchise_network.franchise.infrastructure.entrypoints.dto.BranchProductDTO;
import com.franchise_network.franchise.infrastructure.entrypoints.dto.TopProductByBranchResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IBranchProductMapper {
    BranchProduct toModel(BranchProductDTO dto);

    TopProductByBranchResponseDTO toDTO(ProductWithBranch model);

}