package com.franchise_network.franchise.infrastructure.entrypoints.mapper;

import com.franchise_network.franchise.domain.model.BranchProduct;
import com.franchise_network.franchise.infrastructure.entrypoints.dto.BranchProductDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IBranchProductMapper {
    BranchProduct toModel(BranchProductDTO dto);
}