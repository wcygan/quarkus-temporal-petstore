package com.melloware.petstore.common.models.json;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 *
 
 */
@SuperBuilder(toBuilder = true)
@Getter
@ToString
@Jacksonized
public class RequiredFieldsBaseBuilder {

    @NonNull
    @NotBlank
    private final String requestedByUser;
    
    @NonNull
    @NotBlank
    private final String requestedByHost;

}