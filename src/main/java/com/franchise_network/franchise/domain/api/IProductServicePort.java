package com.franchise_network.franchise.domain.api;

import com.franchise_network.franchise.domain.model.Product;
import reactor.core.publisher.Mono;

public interface IProductServicePort {
    Mono<Product> createProduct(Product product);
    Mono<Product> updateProductName(Long id, String newName);
}
