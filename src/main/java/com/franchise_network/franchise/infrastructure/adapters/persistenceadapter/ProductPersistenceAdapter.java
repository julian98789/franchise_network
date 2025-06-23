package com.franchise_network.franchise.infrastructure.adapters.persistenceadapter;

import com.franchise_network.franchise.domain.model.Product;
import com.franchise_network.franchise.domain.spi.IProductPersistencePort;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.mapper.IProductEntityMapper;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.repository.IProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductPersistenceAdapter implements IProductPersistencePort {

    private final IProductRepository repository;
    private final IProductEntityMapper mapper;

    @Override
    public Mono<Product> save(Product product) {
        return repository.save(mapper.toEntity(product))
                .map(mapper::toModel);
    }

    @Override
    public Mono<Product> findById(Long id) {
        return repository.findById(id)
                .map(mapper::toModel);
    }

    @Override
    public Flux<Product> findAll() {
        return repository.findAll()
                .map(mapper::toModel);
    }
    @Override
    public Mono<Boolean> existsById(Long id) {
        return repository.existsById(id);
    }
}