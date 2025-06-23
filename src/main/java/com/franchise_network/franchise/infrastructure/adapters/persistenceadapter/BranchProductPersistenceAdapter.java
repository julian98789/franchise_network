package com.franchise_network.franchise.infrastructure.adapters.persistenceadapter;

import com.franchise_network.franchise.domain.model.BranchProduct;
import com.franchise_network.franchise.domain.spi.IBranchProductPersistencePort;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.entity.BranchProductEntity;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.mapper.IBranchProductEntityMapper;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.repository.IBranchProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class BranchProductPersistenceAdapter implements IBranchProductPersistencePort {

    private final IBranchProductRepository repository;
    private final IBranchProductEntityMapper entityMapper;

    @Override
    public Mono<BranchProduct> save(BranchProduct branchProduct) {
        BranchProductEntity entity = entityMapper.toEntity(branchProduct);
        return repository.save(entity)
                .map(entityMapper::toModel);
    }

    @Override
    public Mono<Boolean> existsByBranchIdAndProductId(Long branchId, Long productId) {
        return repository.existsByBranchIdAndProductId(branchId, productId);
    }

    @Override
    public Mono<Void> deleteByBranchIdAndProductId(Long branchId, Long productId) {
        return repository.deleteByBranchIdAndProductId(branchId, productId);
    }

}