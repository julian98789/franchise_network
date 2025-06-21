package com.franchise_network.franchise.infrastructure.entrypoints.handler;

import com.franchise_network.franchise.domain.api.IBranchServicePort;
import com.franchise_network.franchise.domain.enums.TechnicalMessage;
import com.franchise_network.franchise.domain.exceptions.BusinessException;
import com.franchise_network.franchise.domain.exceptions.TechnicalException;
import com.franchise_network.franchise.infrastructure.entrypoints.dto.BranchDTO;
import com.franchise_network.franchise.infrastructure.entrypoints.mapper.IBranchMapper;
import com.franchise_network.franchise.infrastructure.entrypoints.util.APIResponse;
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
public class BranchHandler {

    private final IBranchServicePort branchServicePort;
    private final IBranchMapper branchMapper;

    public Mono<ServerResponse> addBranchToFranchise(ServerRequest request) {
        return request.bodyToMono(BranchDTO.class)
                .flatMap(dto -> branchServicePort.addBranchToFranchise(branchMapper.branchDTOToBranch(dto)))
                .doOnSuccess(branch -> log.info("Branch added to franchise successfully"))
                .flatMap(savedBranch -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .bodyValue(TechnicalMessage.BRANCH_ADDED.getMessage()))
                .doOnError(ex -> log.error("Error while adding branch", ex))
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

    private Mono<ServerResponse> buildErrorResponse(HttpStatus httpStatus, TechnicalMessage error,
                                                    List<ErrorDTO> errors) {
        return Mono.defer(() -> {
            APIResponse apiErrorResponse = APIResponse.builder()
                    .code(error.getCode())
                    .message(error.getMessage())
                    .date(Instant.now().toString())
                    .errors(errors)
                    .build();
            return ServerResponse.status(httpStatus).bodyValue(apiErrorResponse);
        });
    }
}