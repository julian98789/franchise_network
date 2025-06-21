package com.franchise_network.franchise.config;

import com.franchise_network.franchise.domain.api.IBranchServicePort;
import com.franchise_network.franchise.domain.api.IFranchiseServicePort;
import com.franchise_network.franchise.domain.spi.IBranchPersistencePort;
import com.franchise_network.franchise.domain.spi.IFranchisePersistencePort;
import com.franchise_network.franchise.domain.usecase.BranchUseCase;
import com.franchise_network.franchise.domain.usecase.FranchiseUseCase;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.BranchPersistenceAdapter;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.FranchisePersistenceAdapter;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.mapper.IBranchEntityMapper;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.mapper.IFranchiseEntityMapper;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.repository.IBranchRepository;
import com.franchise_network.franchise.infrastructure.adapters.persistenceadapter.repository.IFranchiseRepository;
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





}