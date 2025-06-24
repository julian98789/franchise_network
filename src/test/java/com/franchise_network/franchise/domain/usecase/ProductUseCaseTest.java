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
        Product product = new Product(1L, "Producto A");

        when(persistencePort.save(product)).thenReturn(Mono.just(product));

        StepVerifier.create(useCase.createProduct(product))
                .expectNext(product)
                .verifyComplete();

        verify(persistencePort).save(product);
    }

    @Test
    void createProduct_nullName_shouldThrowError() {
        Product product = new Product(1L, null);

        StepVerifier.create(useCase.createProduct(product))
                .expectErrorMatches(error ->
                        error instanceof BusinessException &&
                                error.getMessage().equals(TechnicalMessage.INVALID_PRODUCT_NAME.getMessage()))
                .verify();

        verifyNoInteractions(persistencePort);
    }

    @Test
    void createProduct_blankName_shouldThrowError() {
        Product product = new Product(1L, "  ");

        StepVerifier.create(useCase.createProduct(product))
                .expectErrorMatches(error ->
                        error instanceof BusinessException &&
                                error.getMessage().equals(TechnicalMessage.INVALID_PRODUCT_NAME.getMessage()))
                .verify();

        verifyNoInteractions(persistencePort);
    }

    @Test
    void createProduct_nameTooLong_shouldThrowError() {
        String longName = "P".repeat(101);
        Product product = new Product(1L, longName);

        StepVerifier.create(useCase.createProduct(product))
                .expectErrorMatches(error ->
                        error instanceof BusinessException &&
                                error.getMessage().equals(TechnicalMessage.INVALID_PRODUCT_NAME.getMessage()))
                .verify();

        verifyNoInteractions(persistencePort);
    }
}
