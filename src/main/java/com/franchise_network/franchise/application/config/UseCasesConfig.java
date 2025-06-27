package com.franchise_network.franchise.application.config;

import com.franchise_network.franchise.domain.api.IBranchProductServicePort;
import com.franchise_network.franchise.domain.api.IBranchServicePort;
import com.franchise_network.franchise.domain.api.IFranchiseServicePort;
import com.franchise_network.franchise.domain.api.IProductServicePort;
import com.franchise_network.franchise.domain.spi.IBranchPersistencePort;
import com.franchise_network.franchise.domain.spi.IBranchProductPersistencePort;
import com.franchise_network.franchise.domain.spi.IFranchisePersistencePort;
import com.franchise_network.franchise.domain.spi.IProductPersistencePort;
import com.franchise_network.franchise.domain.usecase.BranchProductUseCase;
import com.franchise_network.franchise.domain.usecase.BranchUseCase;
import com.franchise_network.franchise.domain.usecase.FranchiseUseCase;
import com.franchise_network.franchise.domain.usecase.ProductUseCase;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.BranchPersistenceAdapter;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.BranchProductPersistenceAdapter;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.FranchisePersistenceAdapter;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.ProductPersistenceAdapter;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.mapper.IBranchEntityMapper;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.mapper.IBranchProductEntityMapper;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.mapper.IFranchiseEntityMapper;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.mapper.IProductEntityMapper;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.repository.IBranchProductRepository;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.repository.IBranchRepository;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.repository.IFranchiseRepository;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.repository.IProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class UseCasesConfig {

        private final IFranchiseRepository franchiseRepository;
        private final IFranchiseEntityMapper franchiseEntityMapper;
        private final IBranchRepository branchRepository;
        private final IBranchEntityMapper branchEntityMapper;
        private final IProductRepository productRepository;
        private final IProductEntityMapper productEntityMapper;
        private final IBranchProductRepository branchProductRepository;
        private final IBranchProductEntityMapper branchProductEntityMapper;

        @Bean
        public IFranchisePersistencePort franchisePersistencePort() {
                return new FranchisePersistenceAdapter(franchiseRepository, franchiseEntityMapper);
        }
        @Bean
        public IFranchiseServicePort franchiseServicePort(IFranchisePersistencePort franchisePersistencePort) {
                return new FranchiseUseCase(franchisePersistencePort);
        }
        @Bean
        public IBranchPersistencePort branchPersistencePort() {
                return new BranchPersistenceAdapter(branchRepository, branchEntityMapper);
        }
        @Bean
        public IBranchServicePort branchServicePort(IBranchPersistencePort branchPersistencePort,
                                                    IFranchisePersistencePort franchisePersistencePort) {
                return new BranchUseCase(branchPersistencePort,franchisePersistencePort);
        }
        @Bean
        public IProductPersistencePort productPersistencePort() {
                return new ProductPersistenceAdapter(productRepository, productEntityMapper);
        }
        @Bean
        public IProductServicePort productServicePort(IProductPersistencePort productPersistencePort) {
                return new ProductUseCase(productPersistencePort);
        }
        @Bean
        public IBranchProductPersistencePort branchProductPersistencePort() {
                return new BranchProductPersistenceAdapter(branchProductRepository, branchProductEntityMapper);
        }
        @Bean
        public IBranchProductServicePort branchProductServicePort(IBranchProductPersistencePort branchProductPersistencePort,
                                                                  IProductPersistencePort productPersistencePort,
                                                                  IBranchPersistencePort branchPersistencePort) {
                return new BranchProductUseCase(
                        branchProductPersistencePort,
                        productPersistencePort,
                        branchPersistencePort);
        }





}