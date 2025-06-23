package com.franchise_network.franchise.infrastructure.adapters.persistenceadapter;

import com.franchise_network.franchise.domain.model.Branch;
import com.franchise_network.franchise.domain.spi.IBranchPersistencePort;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.mapper.IBranchEntityMapper;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.repository.IBranchRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class BranchPersistenceAdapter implements IBranchPersistencePort {

    private final IBranchRepository repository;
    private final IBranchEntityMapper mapper;

    @Override
    public Mono<Branch> save(Branch branch) {
        return repository.save(mapper.toEntity(branch))
                .map(mapper::toModel);
    }
    @Override
    public Mono<Boolean> existsById(Long id) {
        return repository.existsById(id);
    }

    @Override
    public Mono<Boolean> existsByNameAndFranchiseId(String name, Long franchiseId) {
        return repository.existsByNameAndFranchiseId(name, franchiseId);
    }
}
