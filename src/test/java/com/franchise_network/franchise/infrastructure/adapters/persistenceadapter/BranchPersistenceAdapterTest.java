package com.franchise_network.franchise.infrastructure.adapters.persistenceadapter;

import com.franchise_network.franchise.domain.model.Branch;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.entity.BranchEntity;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.mapper.IBranchEntityMapper;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.repository.IBranchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BranchPersistenceAdapterTest {

    @Mock
    private IBranchRepository repository;

    @Mock
    private IBranchEntityMapper mapper;

    private BranchPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new BranchPersistenceAdapter(repository, mapper);
    }

    @Test
    void save_success() {
        Branch domainBranch = new Branch(1L, "branch 1", 1L);
        BranchEntity entity = new BranchEntity(1L, "branch 1", 1L);

        when(mapper.toEntity(domainBranch)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(Mono.just(entity));
        when(mapper.toModel(entity)).thenReturn(domainBranch);

        StepVerifier.create(adapter.save(domainBranch))
                .expectNext(domainBranch)
                .verifyComplete();
    }

    @Test
    void existsById_returnsTrue() {
        when(repository.existsById(1L)).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.existsById(1L))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void existsByNameAndFranchiseId_returnsFalse() {
        when(repository.existsByNameAndFranchiseId("branch", 1L)).thenReturn(Mono.just(false));

        StepVerifier.create(adapter.existsByNameAndFranchiseId("branch", 1L))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void findByFranchiseId_success() {
        BranchEntity entity = new BranchEntity(1L, "branch", 1L);
        Branch model = new Branch(1L, "branch", 1L);

        when(repository.findByFranchiseId(1L)).thenReturn(Flux.just(entity));
        when(mapper.toModel(entity)).thenReturn(model);

        StepVerifier.create(adapter.findByFranchiseId(1L))
                .expectNext(model)
                .verifyComplete();
    }
}
