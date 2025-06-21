package com.franchise_network.franchise.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TechnicalMessage {

    INTERNAL_ERROR("500","Something went wrong, please try again", ""),
    INVALID_FRANCHISE_NAME("400", "Invalid franchise name. Must not be empty, unique, and max 100 chars.", "name"),
    FRANCHISE_ALREADY_EXISTS("409", "Franchise name already exists.", "name"),
    FRANCHISE_CREATED("201", "Franchise created successfully.", "");

    private final String code;
    private final String message;
    private final String param;

}