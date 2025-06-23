package com.franchise_network.franchise.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TechnicalMessage {

    INTERNAL_ERROR("500","Something went wrong, please try again", ""),
    INVALID_FRANCHISE_NAME("400", "Invalid franchise name. Must not be empty, unique, and max 100 chars.", "name"),
    FRANCHISE_ALREADY_EXISTS("409", "Franchise name already exists.", "name"),
    FRANCHISE_CREATED("201", "Franchise created successfully.", ""),
    INVALID_BRANCH_NAME("400", "Invalid branch name. Must not be empty, and max 100 chars.", "name"),
    FRANCHISE_NOT_FOUND("404", "Franchise not found.", "id"),
    BRANCH_ADDED("201", "Branch added to franchise successfully.", ""),
    INVALID_PRODUCT_NAME("400", "Invalid product name. Must not be empty, unique, and max 100 chars.", "name"),
    PRODUCT_CREATED("201", "Product created successfully.", ""),
    PRODUCT_ASSIGNED_TO_BRANCH("201", "Product assigned to branch successfully.", ""),
    PRODUCT_NOT_FOUND("404", "Product not found.", "id"),
    BRANCH_NOT_FOUND("404", "Branch not found.", "id"),
    PRODUCT_ALREADY_ASSIGNED("409", "Product is already assigned to this branch.", "productId");

    private final String code;
    private final String message;
    private final String param;

}