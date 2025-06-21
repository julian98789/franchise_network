package com.franchise_network.franchise.infrastructure.entrypoints;

import com.franchise_network.franchise.infrastructure.entrypoints.handler.BranchHandler;
import com.franchise_network.franchise.infrastructure.entrypoints.handler.FranchiseHandler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @Bean
    public RouterFunction<ServerResponse> routerFunction(FranchiseHandler handler,
                                                         BranchHandler branchHandler) {
        return route(POST("/api/v1/register-franchise"), handler::createFranchise)
                .andRoute(POST("/api/v1/register-branch"), branchHandler::addBranchToFranchise);
    }

}