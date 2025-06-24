package com.franchise_network.franchise.domain.usecase;

import com.franchise_network.franchise.domain.enums.TechnicalMessage;
import com.franchise_network.franchise.domain.exceptions.BusinessException;
import com.franchise_network.franchise.domain.model.Branch;
import com.franchise_network.franchise.domain.model.BranchProduct;
import com.franchise_network.franchise.domain.model.Product;
import com.franchise_network.franchise.domain.spi.IBranchPersistencePort;
import com.franchise_network.franchise.domain.spi.IBranchProductPersistencePort;
import com.franchise_network.franchise.domain.spi.IProductPersistencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BranchProductUseCaseTest {

    @Mock
    private IBranchProductPersistencePort branchProductPersistencePort;

    @Mock
    private IProductPersistencePort productPersistencePort;

    @Mock
    private IBranchPersistencePort branchPersistencePort;

    @InjectMocks
    private BranchProductUseCase useCase;

    @Test
    void assignProductToBranch_success() {
        BranchProduct branchProduct = new BranchProduct(1L, 2L, 10);

        when(branchPersistencePort.existsById(1L)).thenReturn(Mono.just(true));
        when(productPersistencePort.existsById(2L)).thenReturn(Mono.just(true));
        when(branchProductPersistencePort.existsByBranchIdAndProductId(1L, 2L)).thenReturn(Mono.just(false));
        when(branchProductPersistencePort.save(branchProduct)).thenReturn(Mono.just(branchProduct));

        StepVerifier.create(useCase.assignProductToBranch(branchProduct))
                .expectNext(branchProduct)
                .verifyComplete();
    }

    @Test
    void updateStock_success() {
        BranchProduct branchProduct = new BranchProduct(1L, 2L, 60);

        when(branchProductPersistencePort.findByBranchIdAndProductId(1L, 2L))
                .thenReturn(Mono.just(new BranchProduct(1L, 2L, 30)));
        when(branchProductPersistencePort.updateStock(1L, 2L, 60))
                .thenReturn(Mono.just(branchProduct));

        StepVerifier.create(useCase.updateStock(1L, 2L, 60))
                .expectNext(branchProduct)
                .verifyComplete();
    }

    @Test
    void removeProductFromBranch_success() {
        when(branchProductPersistencePort.existsByBranchIdAndProductId(1L, 2L))
                .thenReturn(Mono.just(true));
        when(branchProductPersistencePort.deleteByBranchIdAndProductId(1L, 2L))
                .thenReturn(Mono.empty());

        StepVerifier.create(useCase.removeProductFromBranch(1L, 2L))
                .verifyComplete();
    }

    @Test
    void getTopProductByStockPerBranch_success() {
        Branch branch = new Branch(1L, "Branch A", 10L);
        BranchProduct bp1 = new BranchProduct(1L, 5L, 30);
        Product product = new Product(5L, "Product X");

        when(branchPersistencePort.findByFranchiseId(10L)).thenReturn(Flux.just(branch));
        when(branchProductPersistencePort.findByBranchId(1L)).thenReturn(Flux.just(bp1));
        when(productPersistencePort.findById(5L)).thenReturn(Mono.just(product));

        StepVerifier.create(useCase.getTopProductByStockPerBranch(10L))
                .expectNextMatches(p -> p.productName().equals("Product X") && p.stock().equals(30))
                .verifyComplete();
    }


    @Test
    void updateStock_productNotFoundInBranch() {
        when(branchProductPersistencePort.findByBranchIdAndProductId(1L, 2L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.updateStock(1L, 2L, 20))
                .expectErrorMatches(err -> err instanceof BusinessException &&
                        ((BusinessException) err).getMessage().equals(TechnicalMessage.PRODUCT_NOT_FOUND_IN_BRANCH.getMessage()))
                .verify();
    }

    @Test
    void validateBranchId_null_shouldThrowError() {
        StepVerifier.create(useCase.assignProductToBranch(new BranchProduct(null, 1L, 5)))
                .expectErrorMatches(error -> error instanceof BusinessException &&
                        error.getMessage().equals(TechnicalMessage.BRANCH_ID_REQUIRED.getMessage()))
                .verify();
    }

    @Test
    void validateProductId_null_shouldThrowError() {
        StepVerifier.create(useCase.assignProductToBranch(new BranchProduct(1L, null, 5)))
                .expectErrorMatches(error -> error instanceof BusinessException &&
                        error.getMessage().equals(TechnicalMessage.PRODUCT_ID_REQUIRED.getMessage()))
                .verify();
    }




}
