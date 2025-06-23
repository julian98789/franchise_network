package com.franchise_network.franchise.domain.usecase;

import com.franchise_network.franchise.domain.api.IBranchProductServicePort;
import com.franchise_network.franchise.domain.enums.TechnicalMessage;
import com.franchise_network.franchise.domain.exceptions.BusinessException;
import com.franchise_network.franchise.domain.model.BranchProduct;
import com.franchise_network.franchise.domain.spi.IBranchPersistencePort;
import com.franchise_network.franchise.domain.spi.IBranchProductPersistencePort;
import com.franchise_network.franchise.domain.spi.IProductPersistencePort;
import reactor.core.publisher.Mono;

public class BranchProductUseCase implements IBranchProductServicePort {

    private final IBranchProductPersistencePort branchProductPersistencePort;
    private final IProductPersistencePort productPersistencePort;
    private final IBranchPersistencePort branchPersistencePort;

    public BranchProductUseCase(
            IBranchProductPersistencePort branchProductPersistencePort,
            IProductPersistencePort productPersistencePort,
            IBranchPersistencePort branchPersistencePort
    ) {
        this.branchProductPersistencePort = branchProductPersistencePort;
        this.productPersistencePort = productPersistencePort;
        this.branchPersistencePort = branchPersistencePort;
    }

    @Override
    public Mono<BranchProduct> assignProductToBranch(BranchProduct branchProduct) {
        return validateBranchId(branchProduct.branchId())
                .flatMap(validBranchId ->
                        validateProductId(branchProduct.productId())
                                .flatMap(validProductId ->
                                        validateStockValue(branchProduct.stock())
                                                .then(validateBranchExists(validBranchId))
                                                .then(validateProductExists(validProductId))
                                                .then(validateBranchProductNotExists(validBranchId, validProductId))
                                                .then(branchProductPersistencePort.save(branchProduct))
                                )
                );
    }

    @Override
    public Mono<Void> removeProductFromBranch(Long branchId, Long productId) {
        return validateBranchProductExists(branchId, productId)
                .then(branchProductPersistencePort.deleteByBranchIdAndProductId(branchId, productId));
    }

    @Override
    public Mono<BranchProduct> updateStock(Long branchId, Long productId, Integer newStock) {
        return validateStockValue(newStock)
                .then(branchProductPersistencePort.findByBranchIdAndProductId(branchId, productId))
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.PRODUCT_NOT_FOUND_IN_BRANCH)))
                .flatMap(existing -> branchProductPersistencePort.updateStock(branchId, productId, newStock));
    }

    private Mono<Void> validateStockValue(Integer stock) {
        if (stock == null) {
            return Mono.error(new BusinessException(TechnicalMessage.STOCK_REQUIRED));
        }
        if (stock < 0) {
            return Mono.error(new BusinessException(TechnicalMessage.STOCK_CANNOT_BE_NEGATIVE));
        }

        return Mono.empty();
    }

    private Mono<Long> validateBranchId(Long branchId) {
        if (branchId == null) {
            return Mono.error(new BusinessException(TechnicalMessage.BRANCH_ID_REQUIRED));
        }
        return Mono.just(branchId);
    }

    private Mono<Long> validateProductId(Long productId) {
        if (productId == null) {
            return Mono.error(new BusinessException(TechnicalMessage.PRODUCT_ID_REQUIRED));
        }
        return Mono.just(productId);
    }


    private Mono<Void> validateBranchProductExists(Long branchId, Long productId) {
        return branchProductPersistencePort.existsByBranchIdAndProductId(branchId, productId)
                .flatMap(exists -> {
                    if (Boolean.FALSE.equals(exists)) {
                        return Mono.error(new BusinessException(TechnicalMessage.PRODUCT_NOT_FOUND_IN_BRANCH));
                    }
                    return Mono.empty();
                });
    }

    private Mono<Void> validateBranchExists(Long branchId) {
        return branchPersistencePort.existsById(branchId)
                .flatMap(exists -> {
                    if (Boolean.FALSE.equals(exists)) {
                        return Mono.error(new BusinessException(TechnicalMessage.BRANCH_NOT_FOUND));
                    }
                    return Mono.empty();
                });
    }

    private Mono<Void> validateProductExists(Long productId) {
        return productPersistencePort.existsById(productId)
                .flatMap(exists -> {
                    if (Boolean.FALSE.equals(exists)) {
                        return Mono.error(new BusinessException(TechnicalMessage.PRODUCT_NOT_FOUND));
                    }
                    return Mono.empty();
                });
    }

    private Mono<Void> validateBranchProductNotExists(Long branchId, Long productId) {
        return branchProductPersistencePort.existsByBranchIdAndProductId(branchId, productId)
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new BusinessException(TechnicalMessage.PRODUCT_ALREADY_ASSIGNED));
                    }
                    return Mono.empty();
                });
    }
}
