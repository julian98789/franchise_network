package com.franchise_network.franchise.infrastructure.entrypoints;

import com.franchise_network.franchise.infrastructure.entrypoints.handler.BranchHandler;
import com.franchise_network.franchise.infrastructure.entrypoints.handler.BranchProductHandler;
import com.franchise_network.franchise.infrastructure.entrypoints.handler.FranchiseHandler;

import com.franchise_network.franchise.infrastructure.entrypoints.handler.ProductHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @Bean
    public RouterFunction<ServerResponse> routerFunction(FranchiseHandler franchiseHandler,
                                                         BranchHandler branchHandler,
                                                         ProductHandler productHandler,
                                                         BranchProductHandler branchProductHandler) {
        return route(POST("/api/v1/register-franchise"),
                franchiseHandler::createFranchise)
                .andRoute(POST("/api/v1/register-branch"),
                        branchHandler::addBranchToFranchise)
                .andRoute(POST("/api/v1/register-product"),
                        productHandler::createProduct)
                .andRoute(POST("/api/v1/assign-product-to-branch"),
                        branchProductHandler::assignProductToBranch)
                .andRoute(DELETE("/api/v1/branches/{branchId}/products/{productId}"),
                        branchProductHandler::removeProductFromBranch)
                .andRoute(PUT("/api/v1/branches/{branchId}/products/{productId}/stock"),
                        branchProductHandler::updateStock);

    }

}