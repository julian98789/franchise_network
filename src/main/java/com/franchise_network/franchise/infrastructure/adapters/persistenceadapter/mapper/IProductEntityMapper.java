package com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.mapper;

import com.franchise_network.franchise.domain.model.Product;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.entity.ProductEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IProductEntityMapper {
    ProductEntity toEntity(Product model);
    Product toModel(ProductEntity entity);
}