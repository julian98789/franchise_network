package com.franchise_network.franchise.domain.usecase;

import com.franchise_network.franchise.domain.enums.TechnicalMessage;
import com.franchise_network.franchise.domain.exceptions.BusinessException;
import com.franchise_network.franchise.domain.model.Product;
import com.franchise_network.franchise.domain.spi.IProductPersistencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ProductUseCaseTest {

    @Mock
    private IProductPersistencePort persistencePort;

    @InjectMocks
    private ProductUseCase useCase;



    @Test
    void createProduct_success() {
        Product product = new Product(1L, "tea");

        when(persistencePort.existsByName("tea")).thenReturn(Mono.just(false));

        when(persistencePort.save(product)).thenReturn(Mono.just(product));

        StepVerifier.create(useCase.createProduct(product))
                .expectNext(product)
                .verifyComplete();


        verify(persistencePort).existsByName("tea");

        verify(persistencePort).save(product);
    }


    @Test
    void updateProductName_success() {
        Long id = 1L;

        String newName = "New name";
        Product updated = new Product(id, newName);

        when(persistencePort.findById(id)).thenReturn(Mono.just(new Product(id, "Previous name")));

        when(persistencePort.existsByName(newName)).thenReturn(Mono.just(false));
        when(persistencePort.save(updated)).thenReturn(Mono.just(updated));

        StepVerifier.create(useCase.updateProductName(id, newName))
                .expectNext(updated)
                .verifyComplete();

        verify(persistencePort).findById(id);
        verify(persistencePort).existsByName(newName);
        verify(persistencePort).save(updated);
    }

    @Test
    void updateProductName_nullId_shouldThrowError() {

        StepVerifier.create(useCase.updateProductName(null, "name"))

                .expectErrorMatches(error ->
                        error instanceof BusinessException &&
                                error.getMessage().equals(TechnicalMessage.PRODUCT_ID_REQUIRED.getMessage()))
                .verify();

        verifyNoInteractions(persistencePort);
    }

    @Test
    void updateProductName_invalidName_shouldThrowError() {
        Long id = 1L;

        when(persistencePort.findById(anyLong())).thenReturn(Mono.just(new Product(id, "Mocked")));

        StepVerifier.create(useCase.updateProductName(id, "   "))
                .expectErrorMatches(error ->
                        error instanceof BusinessException &&
                                error.getMessage().equals(TechnicalMessage.INVALID_PRODUCT_NAME.getMessage()))
                .verify();

        verify(persistencePort, never()).save(any());
        verify(persistencePort, never()).existsByName(any());
    }


    @Test
    void updateProductName_productNotFound_shouldThrowError() {
        Long id = 1L;

        String newName = "New name";


        when(persistencePort.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.updateProductName(id, newName))
                .expectErrorMatches(error ->
                        error instanceof BusinessException &&
                                error.getMessage().equals(TechnicalMessage.PRODUCT_NOT_FOUND.getMessage()))
                .verify();

        verify(persistencePort).findById(id);
        verify(persistencePort, never()).existsByName(any());
        verify(persistencePort, never()).save(any());
    }

}