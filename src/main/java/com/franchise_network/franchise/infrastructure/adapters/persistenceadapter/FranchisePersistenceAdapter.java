package com.franchise_network.franchise.infrastructure.adapters.persistenceadapter;

import com.franchise_network.franchise.domain.model.Franchise;
import com.franchise_network.franchise.domain.spi.IFranchisePersistencePort;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.mapper.IFranchiseEntityMapper;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.repository.IFranchiseRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class FranchisePersistenceAdapter implements IFranchisePersistencePort {

    private final IFranchiseRepository repository;
    private final IFranchiseEntityMapper mapper;

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        return repository.save(mapper.toEntity(franchise))
                .map(mapper::toModel);
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        return repository.existsByName(name);
    }

    @Override
    public Mono<Boolean>existsById(Long id){
        return repository.existsById(id);
    }
}
