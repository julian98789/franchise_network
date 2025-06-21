package com.franchise_network.franchise.domain.model;

public record Branch(
        Long id,
        String name,
        Long franchiseId
) {}
