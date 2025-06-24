package com.franchise_network.franchise.infrastructure.entrypoints.handler;

import com.franchise_network.franchise.domain.api.IBranchProductServicePort;
import com.franchise_network.franchise.domain.enums.TechnicalMessage;
import com.franchise_network.franchise.domain.exceptions.BusinessException;
import com.franchise_network.franchise.domain.exceptions.TechnicalException;
import com.franchise_network.franchise.domain.model.BranchProduct;
import com.franchise_network.franchise.domain.model.ProductWithBranch;
import com.franchise_network.franchise.infrastructure.entrypoints.dto.BranchProductDTO;
import com.franchise_network.franchise.infrastructure.entrypoints.dto.TopProductByBranchResponseDTO;
import com.franchise_network.franchise.infrastructure.entrypoints.dto.UpdateStockDTO;
import com.franchise_network.franchise.infrastructure.entrypoints.mapper.IBranchProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BranchProductHandlerTest {

    @Mock
    private IBranchProductServicePort service;

    @Mock
    private IBranchProductMapper mapper;

    private BranchProductHandler handler;

    @BeforeEach
    void setUp() {
        handler = new BranchProductHandler(service, mapper);
    }

    @Test
    void assignProductToBranch_success() {
        ServerRequest request = mock(ServerRequest.class);
        BranchProductDTO dto = new BranchProductDTO(1L, 2L, 10);

        when(request.bodyToMono(BranchProductDTO.class)).thenReturn(Mono.just(dto));
        when(mapper.toModel(dto)).thenReturn(new BranchProduct(1L, 2L, 10));

        when(service.assignProductToBranch(any())).thenReturn(Mono.empty());

        ServerResponse response = handler.assignProductToBranch(request).block();

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.statusCode());
    }

    @Test
    void updateStock_success() {
        ServerRequest request = mock(ServerRequest.class);
        when(request.pathVariable("branchId")).thenReturn("1");
        when(request.pathVariable("productId")).thenReturn("2");

        UpdateStockDTO dto = new UpdateStockDTO();
        dto.setStock(50);

        when(request.bodyToMono(UpdateStockDTO.class)).thenReturn(Mono.just(dto));
        when(service.updateStock(1L, 2L, 50)).thenReturn(Mono.empty());

        ServerResponse response = handler.updateStock(request).block();
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.statusCode());
    }

    @Test
    void removeProductFromBranch_success() {
        ServerRequest request = mock(ServerRequest.class);
        when(request.pathVariable("branchId")).thenReturn("1");
        when(request.pathVariable("productId")).thenReturn("2");
        when(service.removeProductFromBranch(1L, 2L)).thenReturn(Mono.empty());

        ServerResponse response = handler.removeProductFromBranch(request).block();
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.statusCode());
    }

    @Test
    void getTopProductsByFranchiseId_success() {
        ServerRequest request = mock(ServerRequest.class);
        when(request.pathVariable("franchiseId")).thenReturn("1");

        ProductWithBranch model = new ProductWithBranch(1L, "branch", 2L, "product", 50);
        TopProductByBranchResponseDTO dto = new TopProductByBranchResponseDTO();
        dto.setBranchId(1L);
        dto.setProductId(2L);
        dto.setProductName("product");
        dto.setBranchName("branch");
        dto.setStock(50);

        when(service.getTopProductByStockPerBranch(1L)).thenReturn(Flux.just(model));
        when(mapper.toDTO(model)).thenReturn(dto);

        ServerResponse response = handler.getTopProductsByFranchiseId(request).block();
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.statusCode());
    }

    @Test
    void assignProductToBranch_businessException() {
        ServerRequest request = mock(ServerRequest.class);
        BranchProductDTO dto = new BranchProductDTO(1L, 2L, 10);
        when(request.bodyToMono(BranchProductDTO.class)).thenReturn(Mono.just(dto));
        when(mapper.toModel(dto)).thenReturn(new BranchProduct(1L, 2L, 10));
        when(service.assignProductToBranch(any())).thenReturn(Mono.error(new BusinessException(TechnicalMessage.INTERNAL_ERROR)));

        ServerResponse response = handler.assignProductToBranch(request).block();
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode());
    }

    @Test
    void assignProductToBranch_technicalException() {
        ServerRequest request = mock(ServerRequest.class);
        BranchProductDTO dto = new BranchProductDTO(1L, 2L, 10);
        when(request.bodyToMono(BranchProductDTO.class)).thenReturn(Mono.just(dto));
        when(mapper.toModel(dto)).thenReturn(new BranchProduct(1L, 2L, 10));
        when(service.assignProductToBranch(any())).thenReturn(Mono.error(new TechnicalException(TechnicalMessage.INTERNAL_ERROR)));

        ServerResponse response = handler.assignProductToBranch(request).block();
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode());
    }

    @Test
    void assignProductToBranch_unexpectedException() {
        ServerRequest request = mock(ServerRequest.class);
        BranchProductDTO dto = new BranchProductDTO(1L, 2L, 10);
        when(request.bodyToMono(BranchProductDTO.class)).thenReturn(Mono.just(dto));
        when(mapper.toModel(dto)).thenReturn(new BranchProduct(1L, 2L, 10));
        when(service.assignProductToBranch(any())).thenReturn(Mono.error(new RuntimeException("Unexpected")));

        ServerResponse response = handler.assignProductToBranch(request).block();
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode());
    }
}
