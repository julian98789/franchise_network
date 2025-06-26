package com.franchise_network.franchise.infrastructure.entrypoints.handler;

import com.franchise_network.franchise.domain.api.IProductServicePort;
import com.franchise_network.franchise.domain.enums.TechnicalMessage;
import com.franchise_network.franchise.domain.exceptions.BusinessException;
import com.franchise_network.franchise.domain.exceptions.TechnicalException;
import com.franchise_network.franchise.domain.model.Product;
import com.franchise_network.franchise.infrastructure.entrypoints.dto.ProductDTO;
import com.franchise_network.franchise.infrastructure.entrypoints.dto.UpdateProductNameDTO;
import com.franchise_network.franchise.infrastructure.entrypoints.mapper.IProductMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ProductHandlerTest {

    @Mock
    private IProductServicePort service;

    @Mock
    private IProductMapper mapper;

    @InjectMocks
    private ProductHandler handler;

    @Test
    void createProduct_success() {
        ServerRequest request = mock(ServerRequest.class);
        ProductDTO dto = new ProductDTO();
        dto.setName("Coffee");

        Product product = new Product(1L, "Coffee");

        when(request.bodyToMono(ProductDTO.class)).thenReturn(Mono.just(dto));
        when(mapper.toModel(dto)).thenReturn(product);
        when(service.createProduct(product)).thenReturn(Mono.just(product));

        StepVerifier.create(handler.createProduct(request))
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.CREATED))
                .verifyComplete();
    }

    @Test
    void createProduct_businessException() {
        ServerRequest request = mock(ServerRequest.class);
        ProductDTO dto = new ProductDTO();
        dto.setName("Duplicate");

        when(request.bodyToMono(ProductDTO.class)).thenReturn(Mono.just(dto));
        when(mapper.toModel(dto)).thenReturn(new Product(null, "Duplicate"));
        when(service.createProduct(any()))
                .thenReturn(Mono.error(new BusinessException(TechnicalMessage.INVALID_PRODUCT_NAME)));

        StepVerifier.create(handler.createProduct(request))
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.BAD_REQUEST))
                .verifyComplete();
    }

    @Test
    void createProduct_technicalException() {
        ServerRequest request = mock(ServerRequest.class);
        ProductDTO dto = new ProductDTO();
        dto.setName("Technical");

        when(request.bodyToMono(ProductDTO.class)).thenReturn(Mono.just(dto));
        when(mapper.toModel(dto)).thenReturn(new Product(null, "Technical"));
        when(service.createProduct(any()))
                .thenReturn(Mono.error(new TechnicalException(TechnicalMessage.INTERNAL_ERROR)));

        StepVerifier.create(handler.createProduct(request))
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR))
                .verifyComplete();
    }

    @Test
    void createProduct_unexpectedException() {
        ServerRequest request = mock(ServerRequest.class);
        ProductDTO dto = new ProductDTO();
        dto.setName("Unexpected error");

        when(request.bodyToMono(ProductDTO.class)).thenReturn(Mono.just(dto));
        when(mapper.toModel(dto)).thenReturn(new Product(null, "Unexpected error"));
        when(service.createProduct(any()))
                .thenReturn(Mono.error(new RuntimeException("General failure")));

        StepVerifier.create(handler.createProduct(request))
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR))
                .verifyComplete();
    }
    @Test
    void updateProductName_success() {
        ServerRequest request = mock(ServerRequest.class);
        UpdateProductNameDTO dto = new UpdateProductNameDTO("new name");
        Long productId = 1L;
        Product updated = new Product(productId, "new name");

        when(request.pathVariable("productId")).thenReturn(productId.toString());
        when(request.bodyToMono(UpdateProductNameDTO.class)).thenReturn(Mono.just(dto));
        when(service.updateProductName(productId, dto.getName())).thenReturn(Mono.just(updated));

        StepVerifier.create(handler.updateProductName(request))
                .expectNextMatches(response -> response.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void updateProductName_businessException() {
        ServerRequest request = mock(ServerRequest.class);
        UpdateProductNameDTO dto = new UpdateProductNameDTO("Duplicate");
        Long productId = 1L;

        when(request.pathVariable("productId")).thenReturn(productId.toString());
        when(request.bodyToMono(UpdateProductNameDTO.class)).thenReturn(Mono.just(dto));
        when(service.updateProductName(productId, dto.getName()))
                .thenReturn(Mono.error(new BusinessException(TechnicalMessage.PRODUCT_NAME_ALREADY_EXISTS)));

        StepVerifier.create(handler.updateProductName(request))
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.BAD_REQUEST))
                .verifyComplete();
    }

    @Test
    void updateProductName_technicalException() {
        ServerRequest request = mock(ServerRequest.class);
        UpdateProductNameDTO dto = new UpdateProductNameDTO("new name");
        Long productId = 1L;

        when(request.pathVariable("productId")).thenReturn(productId.toString());
        when(request.bodyToMono(UpdateProductNameDTO.class)).thenReturn(Mono.just(dto));
        when(service.updateProductName(productId, dto.getName()))
                .thenReturn(Mono.error(new TechnicalException(TechnicalMessage.INTERNAL_ERROR)));

        StepVerifier.create(handler.updateProductName(request))
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR))
                .verifyComplete();
    }

    @Test
    void updateProductName_unexpectedException() {
        ServerRequest request = mock(ServerRequest.class);
        UpdateProductNameDTO dto = new UpdateProductNameDTO("name");
        Long productId = 1L;

        when(request.pathVariable("productId")).thenReturn(productId.toString());
        when(request.bodyToMono(UpdateProductNameDTO.class)).thenReturn(Mono.just(dto));
        when(service.updateProductName(productId, dto.getName()))
                .thenReturn(Mono.error(new RuntimeException("Unexpected error")));

        StepVerifier.create(handler.updateProductName(request))
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR))
                .verifyComplete();
    }

}