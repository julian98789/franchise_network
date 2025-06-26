package com.franchise_network.franchise.infrastructure.adapters.persistenceadapter;

import com.franchise_network.franchise.domain.model.BranchProduct;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.entity.BranchProductEntity;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.mapper.IBranchProductEntityMapper;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.repository.IBranchProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BranchProductPersistenceAdapterTest {

    @Mock
    private IBranchProductRepository repository;

    @Mock
    private IBranchProductEntityMapper mapper;

    private BranchProductPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new BranchProductPersistenceAdapter(repository, mapper);
    }

    @Test
    void save_success() {
        BranchProduct domain = new BranchProduct(1L, 2L, 50);
        BranchProductEntity entity = new BranchProductEntity();
        entity.setBranchId(1L);
        entity.setProductId(2L);
        entity.setStock(50);

        when(mapper.toEntity(domain)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(Mono.just(entity));
        when(mapper.toModel(entity)).thenReturn(domain);

        StepVerifier.create(adapter.save(domain))
                .expectNext(domain)
                .verifyComplete();
    }

    @Test
    void existsByBranchIdAndProductId_success() {
        when(repository.existsByBranchIdAndProductId(1L, 2L)).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.existsByBranchIdAndProductId(1L, 2L))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void deleteByBranchIdAndProductId_success() {
        when(repository.deleteByBranchIdAndProductId(1L, 2L)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.deleteByBranchIdAndProductId(1L, 2L))
                .verifyComplete();
    }

    @Test
    void findByBranchIdAndProductId_success() {
        BranchProductEntity entity = new BranchProductEntity();
        entity.setBranchId(1L);
        entity.setProductId(2L);
        entity.setStock(10);

        BranchProduct model = new BranchProduct(1L, 2L, 10);

        when(repository.findByBranchIdAndProductId(1L, 2L)).thenReturn(Mono.just(entity));
        when(mapper.toModel(entity)).thenReturn(model);

        StepVerifier.create(adapter.findByBranchIdAndProductId(1L, 2L))
                .expectNext(model)
                .verifyComplete();
    }

    @Test
    void updateStock_success() {
        when(repository.updateStock(1L, 2L, 60)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.updateStock(1L, 2L, 60))
                .expectNextMatches(result ->
                        result.branchId().equals(1L) &&
                                result.productId().equals(2L) &&
                                result.stock().equals(60))
                .verifyComplete();
    }

    @Test
    void findByBranchId_success() {
        BranchProductEntity entity = new BranchProductEntity();
        entity.setBranchId(1L);
        entity.setProductId(2L);
        entity.setStock(99);

        BranchProduct model = new BranchProduct(1L, 2L, 99);

        when(repository.findByBranchId(1L)).thenReturn(Flux.just(entity));
        when(mapper.toModel(entity)).thenReturn(model);

        StepVerifier.create(adapter.findByBranchId(1L))
                .expectNext(model)
                .verifyComplete();
    }
}
