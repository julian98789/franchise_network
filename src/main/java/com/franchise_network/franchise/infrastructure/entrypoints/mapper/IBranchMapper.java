package com.franchise_network.franchise.infrastructure.entrypoints.mapper;

import com.franchise_network.franchise.domain.model.Branch;
import com.franchise_network.franchise.infrastructure.entrypoints.dto.BranchDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IBranchMapper {
    @Mapping(target = "id", ignore = true)
    Branch branchDTOToBranch(BranchDTO dto);

    BranchDTO branchToBranchDTO(Branch model);
}