package com.franchise_network.franchise.infrastructure.entrypoints.handler;


import com.franchise_network.franchise.domain.api.IBranchServicePort;
import com.franchise_network.franchise.domain.enums.TechnicalMessage;
import com.franchise_network.franchise.domain.exceptions.BusinessException;
import com.franchise_network.franchise.domain.model.Branch;
import com.franchise_network.franchise.infrastructure.entrypoints.dto.BranchDTO;
import com.franchise_network.franchise.infrastructure.entrypoints.mapper.IBranchMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;
import com.franchise_network.franchise.domain.exceptions.TechnicalException;
import org.mockito.Mock;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;


import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class BranchHandlerTest {

    @Mock
    private IBranchServicePort branchServicePort;

    @Mock
    private IBranchMapper branchMapper;

    private BranchHandler handler;

    @BeforeEach
    void setUp() {
        handler = new BranchHandler(branchServicePort, branchMapper);
    }

    @Test
    void addBranchToFranchise_success() {
        ServerRequest request = mock(ServerRequest.class);
        BranchDTO dto = new BranchDTO("Sucursal A", 1L);
        Branch branch = new Branch(null, "Sucursal A", 1L);

        when(request.bodyToMono(BranchDTO.class)).thenReturn(Mono.just(dto));
        when(branchMapper.branchDTOToBranch(dto)).thenReturn(branch);
        when(branchServicePort.addBranchToFranchise(branch)).thenReturn(Mono.just(branch));

        ServerResponse response = handler.addBranchToFranchise(request).block();
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.statusCode());
    }

    @Test
    void addBranchToFranchise_businessException() {
        ServerRequest request = mock(ServerRequest.class);
        BranchDTO dto = new BranchDTO("Sucursal B", 1L);
        Branch branch = new Branch(null, "Sucursal B", 1L);

        when(request.bodyToMono(BranchDTO.class)).thenReturn(Mono.just(dto));
        when(branchMapper.branchDTOToBranch(dto)).thenReturn(branch);
        when(branchServicePort.addBranchToFranchise(branch))
                .thenReturn(Mono.error(new BusinessException(TechnicalMessage.BRANCH_NAME_ALREADY_EXISTS)));

        ServerResponse response = handler.addBranchToFranchise(request).block();
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode());
    }

    @Test
    void addBranchToFranchise_technicalException() {
        ServerRequest request = mock(ServerRequest.class);
        BranchDTO dto = new BranchDTO("Sucursal C", 1L);
        Branch branch = new Branch(null, "Sucursal C", 1L);

        when(request.bodyToMono(BranchDTO.class)).thenReturn(Mono.just(dto));
        when(branchMapper.branchDTOToBranch(dto)).thenReturn(branch);
        when(branchServicePort.addBranchToFranchise(branch))
                .thenReturn(Mono.error(new TechnicalException(TechnicalMessage.INTERNAL_ERROR)));

        ServerResponse response = handler.addBranchToFranchise(request).block();
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode());
    }

    @Test
    void addBranchToFranchise_unexpectedException() {
        ServerRequest request = mock(ServerRequest.class);
        BranchDTO dto = new BranchDTO("Sucursal D", 1L);
        Branch branch = new Branch(null, "Sucursal D", 1L);

        when(request.bodyToMono(BranchDTO.class)).thenReturn(Mono.just(dto));
        when(branchMapper.branchDTOToBranch(dto)).thenReturn(branch);
        when(branchServicePort.addBranchToFranchise(branch))
                .thenReturn(Mono.error(new RuntimeException("Unexpected")));

        ServerResponse response = handler.addBranchToFranchise(request).block();
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode());
    }
}
