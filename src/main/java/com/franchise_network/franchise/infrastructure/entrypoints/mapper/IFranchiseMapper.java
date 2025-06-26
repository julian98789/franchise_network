package com.franchise_network.franchise.infrastructure.entrypoints.mapper;

import com.franchise_network.franchise.domain.model.Franchise;
import com.franchise_network.franchise.infrastructure.entrypoints.dto.FranchiseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IFranchiseMapper {
    @Mapping(target = "id", ignore = true)
    Franchise franchiseDTOToFranchise(FranchiseDTO dto);
}