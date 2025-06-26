package com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.mapper;

import com.franchise_network.franchise.domain.model.Franchise;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.entity.FranchiseEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IFranchiseEntityMapper {
    FranchiseEntity toEntity(Franchise model);
    Franchise toModel(FranchiseEntity entity);
}
