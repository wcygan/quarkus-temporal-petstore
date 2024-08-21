package com.mycompany.order.purchasing.shared.models.json;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import java.util.List;

/**
 * Checks inventory for given product list
 * 
 
 */
@Builder
@Getter
@ToString
@Jacksonized
public class CheckInventoryRequest {

    /**
     * List of {@link Product} to check
     */
    @NotEmpty
    private final List<Product> products;
}
