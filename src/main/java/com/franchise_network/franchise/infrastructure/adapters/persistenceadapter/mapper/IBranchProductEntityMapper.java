package com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.mapper;

import com.franchise_network.franchise.domain.model.BranchProduct;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.entity.BranchProductEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IBranchProductEntityMapper {
    BranchProductEntity toEntity(BranchProduct model);
    BranchProduct toModel(BranchProductEntity entity);
}