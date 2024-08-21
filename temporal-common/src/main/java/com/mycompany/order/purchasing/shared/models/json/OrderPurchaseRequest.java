package com.mycompany.order.purchasing.shared.models.json;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class OrderPurchaseRequest {

    @NotNull(message = "Credit card information is required")
    private CreditCardInfo creditCard;

    @NotBlank(message = "Customer email is required")
    @Email(message = "Email should be valid")
    private String customerEmail;

    @NotNull(message = "Product list cannot be null")
    private List<@NotNull(message = "Product cannot be null") Product> products;

    
}
