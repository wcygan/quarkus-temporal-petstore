package com.melloware.petstore.common.models.json;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import java.util.List;

/**
 *
 
 */
@Builder
@Getter
@ToString
@Jacksonized
public class MarkOrderCompleteRequest {
    
    @NotBlank
    @Email
    private final String customerEmail;

    @NotNull
    @PastOrPresent
    private final ZonedDateTime orderDate;

    @NotNull
    private final UUID transactionId;

    @NotBlank
    private final String orderNumber;

    @NotEmpty
    private final List<Product> products;
    
    @PositiveOrZero
    private final double orderTotal;
    
}