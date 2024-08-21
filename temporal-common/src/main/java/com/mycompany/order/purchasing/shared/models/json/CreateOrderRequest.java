package com.mycompany.order.purchasing.shared.models.json;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
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
public class CreateOrderRequest extends RequiredFieldsBaseBuilder {
    
    @NotBlank
    @Email
    private final String customerEmail;
    
    @NotNull
    @PastOrPresent
    private final ZonedDateTime orderDate;
    
    @NotNull
    private final UUID transactionId;
    
    @NotEmpty
    private final List<Product> products;
}
