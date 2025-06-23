package com.franchise_network.franchise.domain.usecase;

import com.franchise_network.franchise.domain.api.IProductServicePort;
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
        if (product.name() == null || product.name().isBlank() || product.name().length() > 100) {
            return Mono.error(new BusinessException(TechnicalMessage.INVALID_PRODUCT_NAME));
        }
        return persistencePort.save(product);

    }


}
