package com.melloware.petstore.common.models.json;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

/**
 * Represents a request for purchasing an order in the pet store system.
 * This class encapsulates all the necessary information needed to process an
 * order purchase.
 */
@Builder
@Getter
@ToString
@Jacksonized
@Schema(description = "Request for purchasing an order in the pet store system")
public class OrderPurchaseRequest {

    /**
     * The credit card information for the purchase.
     * This field is required and cannot be null.
     */
    @NotNull(message = "Credit card information is required")
    @Schema(description = "Credit card information for the purchase", required = true)
    private CreditCardInfo creditCard;

    /**
     * The email address of the customer making the purchase.
     * This field is required, cannot be blank, and must be a valid email address.
     */
    @NotBlank(message = "Customer email is required")
    @Email(message = "Email should be valid")
    @Schema(description = "Email address of the customer", required = true, example = "homer.simpson@springfield.gov")
    private String customerEmail;

    /**
     * The list of products being purchased in this order.
     * This field is required, cannot be null, and must contain at least one
     * non-null product.
     */
    @NotNull(message = "Product list cannot be null")
    @Schema(description = "List of products being purchased", required = true, minItems = 1)
    private List<@NotNull(message = "Product cannot be null") Product> products;

}