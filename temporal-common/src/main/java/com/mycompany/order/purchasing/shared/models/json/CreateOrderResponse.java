package com.mycompany.order.purchasing.shared.models.json;

import com.mycompany.order.purchasing.shared.models.enums.OrderStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

/**
 *
 
 */
@Builder(toBuilder = true)
@Getter
@ToString
@Jacksonized
public class CreateOrderResponse {
    
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
    
    @NotNull
    private final OrderStatus status;
}
