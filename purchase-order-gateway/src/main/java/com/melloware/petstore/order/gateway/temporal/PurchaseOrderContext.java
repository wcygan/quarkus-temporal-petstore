package com.melloware.petstore.order.gateway.temporal;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import com.melloware.petstore.common.models.enums.OrderStatus;
import com.melloware.petstore.common.models.json.CreditCardInfo;
import com.melloware.petstore.common.models.json.Product;
import com.melloware.petstore.common.models.json.RequiredFieldsBaseBuilder;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@SuperBuilder(toBuilder = true)
@Getter
@ToString
@Jacksonized
public class PurchaseOrderContext extends RequiredFieldsBaseBuilder {

    @NotNull(message = "Transaction id is required")
    private final UUID transactionId;

    @PastOrPresent
    private final ZonedDateTime requestDate;

    @NotNull(message = "Order status is required")
    private final OrderStatus status;

    @NotNull(message = "Credit card information is required")
    private final CreditCardInfo creditCard;

    @NotBlank(message = "Customer email is required")
    @Email(message = "Email should be valid")
    private final String customerEmail;

    @NotNull(message = "Product list cannot be null")
    private final List<@NotNull(message = "Product cannot be null") Product> products;

    @NotBlank(message = "Order number is required")
    private final String orderNumber;

    // Might not have it at all times so no validations
    private final String trackingNumber;

    // Total amount of order
    private final double orderTotal;

}