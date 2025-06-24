package com.franchise_network.franchise.infrastructure.adapters.persistenceadapter;

import com.franchise_network.franchise.domain.model.Product;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.entity.ProductEntity;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.mapper.IProductEntityMapper;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.repository.IProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductPersistenceAdapterTest {

    @Mock
    private IProductRepository repository;

    @Mock
    private IProductEntityMapper mapper;

    private ProductPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ProductPersistenceAdapter(repository, mapper);
    }

    @Test
    void save_success() {
        Product product = new Product(null, "Café");
        ProductEntity entity = new ProductEntity(null, "Café");
        ProductEntity savedEntity = new ProductEntity(1L, "Café");
        Product savedProduct = new Product(1L, "Café");

        when(mapper.toEntity(product)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(Mono.just(savedEntity));
        when(mapper.toModel(savedEntity)).thenReturn(savedProduct);

        StepVerifier.create(adapter.save(product))
                .expectNext(savedProduct)
                .verifyComplete();

        verify(repository).save(entity);
    }

    @Test
    void findById_success() {
        ProductEntity entity = new ProductEntity(1L, "Café");
        Product product = new Product(1L, "Café");

        when(repository.findById(1L)).thenReturn(Mono.just(entity));
        when(mapper.toModel(entity)).thenReturn(product);

        StepVerifier.create(adapter.findById(1L))
                .expectNext(product)
                .verifyComplete();

        verify(repository).findById(1L);
    }

    @Test
    void existsById_true() {
        when(repository.existsById(1L)).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.existsById(1L))
                .expectNext(true)
                .verifyComplete();

        verify(repository).existsById(1L);
    }

    @Test
    void existsById_false() {
        when(repository.existsById(999L)).thenReturn(Mono.just(false));

        StepVerifier.create(adapter.existsById(999L))
                .expectNext(false)
                .verifyComplete();

        verify(repository).existsById(999L);
    }
}
