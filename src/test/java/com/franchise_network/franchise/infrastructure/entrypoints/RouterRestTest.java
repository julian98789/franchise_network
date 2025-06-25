package com.franchise_network.franchise.infrastructure.entrypoints;

import com.franchise_network.franchise.infrastructure.entrypoints.handler.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouterRestTest {

    @Mock
    private FranchiseHandler franchiseHandler;

    @Mock
    private BranchHandler branchHandler;

    @Mock
    private ProductHandler productHandler;

    @Mock
    private BranchProductHandler branchProductHandler;

    @Mock
    private HealthCheckHandler healthCheckHandler;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        RouterRest routerRest = new RouterRest();
        webTestClient = WebTestClient.bindToRouterFunction(
                routerRest.routerFunction(franchiseHandler, branchHandler, productHandler, branchProductHandler,healthCheckHandler)
        ).build();


        lenient().when(franchiseHandler.createFranchise(any())).thenReturn(ServerResponse.ok().build());
        lenient().when(franchiseHandler.updateFranchiseName(any())).thenReturn(ServerResponse.ok().build());
        lenient().when(branchHandler.addBranchToFranchise(any())).thenReturn(ServerResponse.ok().build());
        lenient().when(branchHandler.updateBranchName(any())).thenReturn(ServerResponse.ok().build());
        lenient().when(productHandler.createProduct(any())).thenReturn(ServerResponse.ok().build());
        lenient().when(productHandler.updateProductName(any())).thenReturn(ServerResponse.ok().build());
        lenient().when(branchProductHandler.assignProductToBranch(any())).thenReturn(ServerResponse.ok().build());
        lenient().when(branchProductHandler.removeProductFromBranch(any())).thenReturn(ServerResponse.noContent().build());
        lenient().when(branchProductHandler.updateStock(any())).thenReturn(ServerResponse.ok().build());
        lenient().when(branchProductHandler.getTopProductsByFranchiseId(any())).thenReturn(ServerResponse.ok().build());
    }

    @Test
    void testRegisterFranchiseRoute() {
        webTestClient.post().uri("/api/v1/register-franchise")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testRegisterBranchRoute() {
        webTestClient.post().uri("/api/v1/register-branch")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testRegisterProductRoute() {
        webTestClient.post().uri("/api/v1/register-product")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testAssignProductToBranchRoute() {
        webTestClient.post().uri("/api/v1/assign-product-to-branch")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testRemoveProductFromBranchRoute() {
        webTestClient.delete().uri("/api/v1/branches/1/products/1")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testUpdateStockRoute() {
        webTestClient.put().uri("/api/v1/branches/1/products/1/stock")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testGetTopProductsByFranchiseIdRoute() {
        webTestClient.get().uri("/api/v1/franchises/1/top-products")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testUpdateFranchiseNameRoute() {
        webTestClient.put().uri("/api/v1/franchises/1/update-name")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testUpdateBranchNameRoute() {
        webTestClient.put().uri("/api/v1/branches/1/update-name")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testUpdateProductNameRoute() {
        webTestClient.put().uri("/api/v1/products/1/update-name")
                .exchange()
                .expectStatus().isOk();
    }
    @Test
    void testHealthCheckRoute() {
        when(healthCheckHandler.health(any())).thenReturn(ServerResponse.ok().build());

        webTestClient.get().uri("/health")
                .exchange()
                .expectStatus().isOk();
    }

}