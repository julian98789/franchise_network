package com.franchise_network.franchise.domain.usecase;

import com.franchise_network.franchise.domain.api.IBranchProductServicePort;
import com.franchise_network.franchise.domain.enums.TechnicalMessage;
import com.franchise_network.franchise.domain.exceptions.BusinessException;
import com.franchise_network.franchise.domain.exceptions.TechnicalException;
import com.franchise_network.franchise.domain.model.Branch;
import com.franchise_network.franchise.domain.model.BranchProduct;
import com.franchise_network.franchise.domain.model.ProductWithBranch;
import com.franchise_network.franchise.domain.spi.IBranchPersistencePort;
import com.franchise_network.franchise.domain.spi.IBranchProductPersistencePort;
import com.franchise_network.franchise.domain.spi.IProductPersistencePort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;

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


    @Override
    public Flux<ProductWithBranch> getTopProductByStockPerBranch(Long franchiseId) {
        return branchPersistencePort.findByFranchiseId(franchiseId)
                .flatMap(branch ->
                        branchProductPersistencePort.findByBranchId(branch.id())
                                .collectList()
                                .flatMapMany(products -> handleTopProduct(branch, products))
                );
    }

    private Flux<ProductWithBranch> handleTopProduct(Branch branch, List<BranchProduct> products) {
        if (isEmpty(products)) return Flux.empty();

        BranchProduct topProduct = getTopProductByStock(products);

        return productPersistencePort.findById(topProduct.productId())
                .map(product -> new ProductWithBranch(
                        branch.id(),
                        branch.name(),
                        product.id(),
                        product.name(),
                        topProduct.stock()
                )).flux();
    }

    private boolean isEmpty(List<BranchProduct> products) {
        return products == null || products.isEmpty();
    }

    private BranchProduct getTopProductByStock(List<BranchProduct> products) {
        return products.stream()
                .max(Comparator.comparingInt(BranchProduct::stock))
                .orElseThrow(() -> new TechnicalException(TechnicalMessage.INTERNAL_ERROR));
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
