package com.franchise_network.franchise.infrastructure.entrypoints;

import com.franchise_network.franchise.infrastructure.entrypoints.dto.*;
import com.franchise_network.franchise.infrastructure.entrypoints.handler.BranchHandler;
import com.franchise_network.franchise.infrastructure.entrypoints.handler.BranchProductHandler;
import com.franchise_network.franchise.infrastructure.entrypoints.handler.FranchiseHandler;

import com.franchise_network.franchise.infrastructure.entrypoints.handler.ProductHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/register-franchise",
                    method = RequestMethod.POST,
                    beanClass = FranchiseHandler.class,
                    beanMethod = "createFranchise",
                    operation = @Operation(
                            operationId = "createFranchise",
                            summary = "Register a new franchise",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = FranchiseDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Franchise successfully created"),
                                    @ApiResponse(responseCode = "400", description = "Bad request"),
                                    @ApiResponse(responseCode = "500", description = "Internal server error")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/register-branch",
                    method = RequestMethod.POST,
                    beanClass = BranchHandler.class,
                    beanMethod = "addBranchToFranchise",
                    operation = @Operation(
                            operationId = "addBranch",
                            summary = "Add a new branch to a franchise",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = BranchDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Branch successfully added")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/register-product",
                    method = RequestMethod.POST,
                    beanClass = ProductHandler.class,
                    beanMethod = "createProduct",
                    operation = @Operation(
                            operationId = "createProduct",
                            summary = "Register a new product",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = ProductDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Product successfully created")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/assign-product-to-branch",
                    method = RequestMethod.POST,
                    beanClass = BranchProductHandler.class,
                    beanMethod = "assignProductToBranch",
                    operation = @Operation(
                            operationId = "assignProduct",
                            summary = "Assign product to a branch",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = BranchProductDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Product assigned to branch")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/branches/{branchId}/products/{productId}",
                    method = RequestMethod.DELETE,
                    beanClass = BranchProductHandler.class,
                    beanMethod = "removeProductFromBranch",
                    operation = @Operation(
                            operationId = "removeProductFromBranch",
                            summary = "Remove a product from a branch",
                            parameters = {
                                    @Parameter(name = "branchId", in = ParameterIn.PATH, required = true),
                                    @Parameter(name = "productId", in = ParameterIn.PATH, required = true)
                            },
                            responses = {
                                    @ApiResponse(responseCode = "204", description = "Product removed")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/branches/{branchId}/products/{productId}/stock",
                    method = RequestMethod.PUT,
                    beanClass = BranchProductHandler.class,
                    beanMethod = "updateStock",
                    operation = @Operation(
                            operationId = "updateStock",
                            summary = "Update stock of a product in a branch",
                            parameters = {
                                    @Parameter(name = "branchId", in = ParameterIn.PATH, required = true),
                                    @Parameter(name = "productId", in = ParameterIn.PATH, required = true)
                            },
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = UpdateStockDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Stock updated")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/franchises/{franchiseId}/top-products",
                    method = RequestMethod.GET,
                    beanClass = BranchProductHandler.class,
                    beanMethod = "getTopProductsByFranchiseId",
                    operation = @Operation(
                            operationId = "getTopProductsByFranchiseId",
                            summary = "Get top product by stock in each branch of a franchise",
                            parameters = {
                                    @Parameter(name = "franchiseId", in = ParameterIn.PATH, required = true)
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "List of top-stocked products per branch",
                                            content = @Content(
                                                    array = @ArraySchema(schema = @Schema(implementation = TopProductByBranchResponseDTO.class))
                                            )
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/franchises/{franchiseId}/update-name",
                    method = RequestMethod.PUT,
                    beanClass = FranchiseHandler.class,
                    beanMethod = "updateFranchiseName",
                    operation = @Operation(
                            operationId = "updateFranchiseName",
                            summary = "Update the name of an existing franchise",
                            parameters = {
                                    @Parameter(name = "franchiseId", in = ParameterIn.PATH, required = true, description = "ID of the franchise to update")
                            },
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = UpdateFranchiseNameDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Franchise name updated"),
                                    @ApiResponse(responseCode = "400", description = "Invalid franchise name or franchise does not exist"),
                                    @ApiResponse(responseCode = "500", description = "Internal server error")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/branches/{branchId}/update-name",
                    method = RequestMethod.PUT,
                    beanClass = BranchHandler.class,
                    beanMethod = "updateBranchName",
                    operation = @Operation(
                            operationId = "updateBranchName",
                            summary = "Update the name of a branch",
                            description = "Updates the name of an existing branch given its ID.",
                            parameters = {
                                    @Parameter(
                                            name = "branchId",
                                            in = ParameterIn.PATH,
                                            required = true,
                                            description = "ID of the branch to update"
                                    )
                            },
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(
                                            schema = @Schema(implementation = UpdateBranchNameDTO.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Branch name successfully updated"
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Invalid branch name or branch does not exist"
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Internal server error"
                                    )
                            }
                    )
            )
    })
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
                        branchProductHandler::updateStock)
                .andRoute(GET("/api/v1/franchises/{franchiseId}/top-products"),
                        branchProductHandler::getTopProductsByFranchiseId)
                .andRoute(PUT("/api/v1/franchises/{franchiseId}/update-name"),
                        franchiseHandler::updateFranchiseName)
                .andRoute(PUT("/api/v1/branches/{branchId}/update-name"),
                        branchHandler::updateBranchName);
    }

}