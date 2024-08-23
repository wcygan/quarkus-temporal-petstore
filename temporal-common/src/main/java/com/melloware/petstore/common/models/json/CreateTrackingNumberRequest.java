package com.melloware.petstore.common.models.json;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

/**
 * Request to create a tracking number
 * 
 * In this example I pass in the list of products ordered
 * 
 
 */
@Builder
@Getter
@ToString
@Jacksonized
public class CreateTrackingNumberRequest {
   
    /**
     * List of {@link Product} ordered
     */
    @NotEmpty
    private final List<Product> products;
    
}