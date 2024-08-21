package com.mycompany.order.purchasing.shared.models.json;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

/**
 *
 
 */
@Builder
@Getter
@ToString
@Jacksonized
public class Product {

    @NotBlank(message = "Product SKU is required")
    private final String sku;

    @Min(value = 1, message = "Quantity must be at least 1")
    private final int quantity;
    
    @Min(value = 1, message = "Price must be at least 1")
    private final double price;

}
