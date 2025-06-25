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
        Product product = new Product(null, "coffee");
        ProductEntity entity = new ProductEntity(null, "coffee");
        ProductEntity savedEntity = new ProductEntity(1L, "coffee");
        Product savedProduct = new Product(1L, "coffee");

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
        ProductEntity entity = new ProductEntity(1L, "coffee");
        Product product = new Product(1L, "coffee");

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

    @Test
    void existsByName_true() {
        when(repository.existsByName("coffee")).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.existsByName("coffee"))
                .expectNext(true)
                .verifyComplete();

        verify(repository).existsByName("coffee");
    }

    @Test
    void existsByName_false() {
        when(repository.existsByName("tea")).thenReturn(Mono.just(false));

        StepVerifier.create(adapter.existsByName("tea"))
                .expectNext(false)
                .verifyComplete();

        verify(repository).existsByName("tea");
    }

}
