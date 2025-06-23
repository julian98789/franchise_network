package com.franchise_network.franchise.domain.spi;

import com.franchise_network.franchise.domain.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IProductPersistencePort {
    Mono<Product> save(Product product);
    Mono<Product> findById(Long id);
    Flux<Product> findAll();
    Mono<Boolean>existsById(Long id);
}
