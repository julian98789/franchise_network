package com.franchise_network.franchise.infrastructure.adapters.persistenceadapter;

import com.franchise_network.franchise.domain.model.Franchise;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.entity.FranchiseEntity;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.mapper.IFranchiseEntityMapper;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.repository.IFranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FranchisePersistenceAdapterTest {

    @Mock
    private IFranchiseRepository repository;

    @Mock
    private IFranchiseEntityMapper mapper;

    private FranchisePersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new FranchisePersistenceAdapter(repository, mapper);
    }

    @Test
    void save_success() {
        Franchise franchise = new Franchise(1L, "Franquicia");
        FranchiseEntity entity = new FranchiseEntity(1L, "Franquicia");

        when(mapper.toEntity(franchise)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(Mono.just(entity));
        when(mapper.toModel(entity)).thenReturn(franchise);

        StepVerifier.create(adapter.save(franchise))
                .expectNext(franchise)
                .verifyComplete();

        verify(repository).save(entity);
    }

    @Test
    void existsByName_returnsTrue() {
        when(repository.existsByName("Franquicia")).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.existsByName("Franquicia"))
                .expectNext(true)
                .verifyComplete();

        verify(repository).existsByName("Franquicia");
    }

    @Test
    void existsById_returnsFalse() {
        when(repository.existsById(100L)).thenReturn(Mono.just(false));

        StepVerifier.create(adapter.existsById(100L))
                .expectNext(false)
                .verifyComplete();

        verify(repository).existsById(100L);
    }
}
