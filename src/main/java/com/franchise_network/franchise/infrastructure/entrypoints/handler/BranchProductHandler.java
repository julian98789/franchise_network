package com.franchise_network.franchise.infrastructure.entrypoints.handler;

import com.franchise_network.franchise.domain.api.IBranchProductServicePort;
import com.franchise_network.franchise.domain.enums.TechnicalMessage;
import com.franchise_network.franchise.domain.exceptions.BusinessException;
import com.franchise_network.franchise.domain.exceptions.TechnicalException;
import com.franchise_network.franchise.infrastructure.entrypoints.dto.BranchProductDTO;
import com.franchise_network.franchise.infrastructure.entrypoints.dto.UpdateStockDTO;
import com.franchise_network.franchise.infrastructure.entrypoints.mapper.IBranchProductMapper;
import com.franchise_network.franchise.infrastructure.entrypoints.util.APIResponse;
import com.franchise_network.franchise.infrastructure.entrypoints.util.Constants;
import com.franchise_network.franchise.infrastructure.entrypoints.util.ErrorDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;


@Component
@RequiredArgsConstructor
@Slf4j
public class BranchProductHandler {

    private final IBranchProductServicePort service;
    private final IBranchProductMapper mapper;

    public Mono<ServerResponse> assignProductToBranch(ServerRequest request) {
        return request.bodyToMono(BranchProductDTO.class)
                .flatMap(dto -> service.assignProductToBranch(mapper.toModel(dto)))
                .doOnSuccess(v -> log.info("Product assigned to branch successfully"))
                .then(ServerResponse
                        .status(HttpStatus.CREATED)
                        .bodyValue(TechnicalMessage.PRODUCT_ASSIGNED_TO_BRANCH.getMessage()))
                .doOnError(ex -> log.error("Error while assigning product to branch", ex))
                .onErrorResume(BusinessException.class, ex -> buildErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        ex.getTechnicalMessage(),
                        List.of(ErrorDTO.builder()
                                .code(ex.getTechnicalMessage().getCode())
                                .message(ex.getTechnicalMessage().getMessage())
                                .param(ex.getTechnicalMessage().getParam())
                                .build())))
                .onErrorResume(TechnicalException.class, ex -> buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        TechnicalMessage.INTERNAL_ERROR,
                        List.of(ErrorDTO.builder()
                                .code(ex.getTechnicalMessage().getCode())
                                .message(ex.getTechnicalMessage().getMessage())
                                .param(ex.getTechnicalMessage().getParam())
                                .build())))
                .onErrorResume(ex -> {
                    log.error("Unexpected error occurred", ex);
                    return buildErrorResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            TechnicalMessage.INTERNAL_ERROR,
                            List.of(ErrorDTO.builder()
                                    .code(TechnicalMessage.INTERNAL_ERROR.getCode())
                                    .message(TechnicalMessage.INTERNAL_ERROR.getMessage())
                                    .build()));
                });
    }


    public Mono<ServerResponse> removeProductFromBranch(ServerRequest request) {
        Long branchId = Long.valueOf(request.pathVariable(Constants.PATH_VARIABLE_BRANCH_ID));
        Long productId = Long.valueOf(request.pathVariable(Constants.PATH_VARIABLE_PRODUCT_ID));

        return service.removeProductFromBranch(branchId, productId)
                .doOnSuccess(v -> log.info("Product removed from branch successfully"))
                .then(ServerResponse.ok().bodyValue(TechnicalMessage.PRODUCT_REMOVED_FROM_BRANCH.getMessage()))
                .onErrorResume(BusinessException.class, ex -> buildErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        ex.getTechnicalMessage(),
                        List.of(ErrorDTO.builder()
                                .code(ex.getTechnicalMessage().getCode())
                                .message(ex.getTechnicalMessage().getMessage())
                                .param(ex.getTechnicalMessage().getParam())
                                .build())))
                .onErrorResume(TechnicalException.class, ex -> buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        TechnicalMessage.INTERNAL_ERROR,
                        List.of(ErrorDTO.builder()
                                .code(ex.getTechnicalMessage().getCode())
                                .message(ex.getTechnicalMessage().getMessage())
                                .param(ex.getTechnicalMessage().getParam())
                                .build())))
                .onErrorResume(ex -> {
                    log.error("Unexpected error occurred", ex);
                    return buildErrorResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            TechnicalMessage.INTERNAL_ERROR,
                            List.of(ErrorDTO.builder()
                                    .code(TechnicalMessage.INTERNAL_ERROR.getCode())
                                    .message(TechnicalMessage.INTERNAL_ERROR.getMessage())
                                    .build()));
                });
    }

    public Mono<ServerResponse> updateStock(ServerRequest request) {
        Long branchId = Long.valueOf(request.pathVariable(Constants.PATH_VARIABLE_BRANCH_ID));
        Long productId = Long.valueOf(request.pathVariable(Constants.PATH_VARIABLE_PRODUCT_ID));

        return request.bodyToMono(UpdateStockDTO.class)
                .flatMap(dto -> service.updateStock(branchId, productId, dto.getStock()))
                .doOnSuccess(v -> log.info("Stock updated successfully"))
                .then(ServerResponse.ok().bodyValue(TechnicalMessage.STOCK_UPDATED.getMessage()))
                .onErrorResume(BusinessException.class, ex -> buildErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        ex.getTechnicalMessage(),
                        List.of(ErrorDTO.builder()
                                .code(ex.getTechnicalMessage().getCode())
                                .message(ex.getTechnicalMessage().getMessage())
                                .param(ex.getTechnicalMessage().getParam())
                                .build())))
                .onErrorResume(TechnicalException.class, ex -> buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        TechnicalMessage.INTERNAL_ERROR,
                        List.of(ErrorDTO.builder()
                                .code(ex.getTechnicalMessage().getCode())
                                .message(ex.getTechnicalMessage().getMessage())
                                .param(ex.getTechnicalMessage().getParam())
                                .build())))
                .onErrorResume(ex -> {
                    log.error("Unexpected error", ex);
                    return buildErrorResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            TechnicalMessage.INTERNAL_ERROR,
                            List.of(ErrorDTO.builder()
                                    .code(TechnicalMessage.INTERNAL_ERROR.getCode())
                                    .message(TechnicalMessage.INTERNAL_ERROR.getMessage())
                                    .build()));
                });
    }

    public Mono<ServerResponse> getTopProductsByFranchiseId(ServerRequest request) {
        Long franchiseId = Long.valueOf(request.pathVariable("franchiseId"));

        return service.getTopProductByStockPerBranch(franchiseId)
                .map(mapper::toDTO)
                .collectList()
                .doOnSuccess(result -> log.info("Top products per branch retrieved for franchiseId={}", franchiseId))
                .flatMap(list -> ServerResponse.ok().bodyValue(list))
                .onErrorResume(BusinessException.class, ex -> buildErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        ex.getTechnicalMessage(),
                        List.of(ErrorDTO.builder()
                                .code(ex.getTechnicalMessage().getCode())
                                .message(ex.getTechnicalMessage().getMessage())
                                .param(ex.getTechnicalMessage().getParam())
                                .build())))
                .onErrorResume(TechnicalException.class, ex -> buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        TechnicalMessage.INTERNAL_ERROR,
                        List.of(ErrorDTO.builder()
                                .code(ex.getTechnicalMessage().getCode())
                                .message(ex.getTechnicalMessage().getMessage())
                                .param(ex.getTechnicalMessage().getParam())
                                .build())))
                .onErrorResume(ex -> {
                    log.error("Unexpected error retrieving top products by franchiseId={}", franchiseId, ex);
                    return buildErrorResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            TechnicalMessage.INTERNAL_ERROR,
                            List.of(ErrorDTO.builder()
                                    .code(TechnicalMessage.INTERNAL_ERROR.getCode())
                                    .message(TechnicalMessage.INTERNAL_ERROR.getMessage())
                                    .build()));
                });
    }




    private Mono<ServerResponse> buildErrorResponse(HttpStatus httpStatus, TechnicalMessage error, List<ErrorDTO> errors) {
        APIResponse apiErrorResponse = APIResponse.builder()
                .code(error.getCode())
                .message(error.getMessage())
                .date(Instant.now().toString())
                .errors(errors)
                .build();
        return ServerResponse.status(httpStatus).bodyValue(apiErrorResponse);
    }
}
