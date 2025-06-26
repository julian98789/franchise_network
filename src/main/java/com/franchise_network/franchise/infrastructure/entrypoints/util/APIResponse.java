package com.franchise_network.franchise.infrastructure.entrypoints.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.franchise_network.franchise.infrastructure.entrypoints.dto.FranchiseDTO;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class APIResponse {
    private String code;
    private String message;
    private String identifier;
    private String date;
    private FranchiseDTO data;
    private List<ErrorDTO> errors;
}
