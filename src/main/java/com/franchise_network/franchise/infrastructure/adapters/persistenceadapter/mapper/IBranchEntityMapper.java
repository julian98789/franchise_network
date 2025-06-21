package com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.mapper;

import com.franchise_network.franchise.domain.model.Branch;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.entity.BranchEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IBranchEntityMapper {

    Branch toModel(BranchEntity entity);

    BranchEntity toEntity(Branch model);
}