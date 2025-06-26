package com.franchise_network.franchise.domain.usecase;

import com.franchise_network.franchise.domain.api.IProductServicePort;
import com.franchise_network.franchise.domain.constants.Constants;
import com.franchise_network.franchise.domain.enums.TechnicalMessage;
import com.franchise_network.franchise.domain.exceptions.BusinessException;
import com.franchise_network.franchise.domain.model.Product;
import com.franchise_network.franchise.domain.spi.IProductPersistencePort;
import reactor.core.publisher.Mono;

public class ProductUseCase implements IProductServicePort {


    private final IProductPersistencePort persistencePort;

    public ProductUseCase(IProductPersistencePort persistencePort) {
        this.persistencePort = persistencePort;
    }

    @Override
    public Mono<Product> createProduct(Product product) {
        return validateProductName(product.name())
                .then(checkProductNameNotExists(product.name()))
                .then(persistencePort.save(product));
    }

    @Override
    public Mono<Product> updateProductName(Long id, String newName) {
        if (id == null) {
            return Mono.error(new BusinessException(TechnicalMessage.PRODUCT_ID_REQUIRED));
        }

        return validateProductName(newName)
                .then(persistencePort.findById(id)
                        .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.PRODUCT_NOT_FOUND)))
                        .flatMap(existing ->
                                checkProductNameNotExists(newName)
                                        .then(persistencePort.save(new Product(id, newName)))
                        ));
    }

    private Mono<Void> validateProductName(String name) {
        if (name == null || name.isBlank() || name.length() > Constants.MAX_PRODUCT_NAME_LENGTH) {
            return Mono.error(new BusinessException(TechnicalMessage.INVALID_PRODUCT_NAME));
        }
        return Mono.empty();
    }

    private Mono<Void> checkProductNameNotExists(String name) {
        return persistencePort.existsByName(name)
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new BusinessException(TechnicalMessage.PRODUCT_NAME_ALREADY_EXISTS));
                    }
                    return Mono.empty();
                });
    }
}
