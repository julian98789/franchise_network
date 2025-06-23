package com.franchise_network.franchise.infrastructure.entrypoints.mapper;

import com.franchise_network.franchise.domain.model.Product;
import com.franchise_network.franchise.infrastructure.entrypoints.dto.ProductDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IProductMapper {
    Product toModel(ProductDTO dto);
}