package com.melloware.petstore.common.models.json;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

/**
 * Represents a product in the pet store system.
 * This class encapsulates the essential information about a product,
 * including its SKU, quantity, and price.
 */
@Builder
@Getter
@ToString
@Jacksonized
@Schema(description = "Represents a product in the pet store system")
public class Product {

    /**
     * The Stock Keeping Unit (SKU) of the product.
     * This is a unique identifier for the product and is required.
     */
    @NotBlank(message = "Product SKU is required")
    @Schema(description = "Unique identifier for the product", required = true, example = "DOG-COLLAR-001")
    private final String sku;

    /**
     * The quantity of the product.
     * This must be at least 1.
     */
    @Min(value = 1, message = "Quantity must be at least 1")
    @Schema(description = "Quantity of the product", minimum = "1", example = "5")
    private final int quantity;

    /**
     * The price of the product.
     * This must be at least 1.
     */
    @Min(value = 1, message = "Price must be at least 1")
    @Schema(description = "Price of the product", minimum = "1.0", example = "19.99")
    private final double price;

}