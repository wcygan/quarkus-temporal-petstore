package com.mycompany.order.purchasing.shared.models.json;

import com.mycompany.order.purchasing.shared.models.enums.PaymentType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
public class CreditCardInfo {
    
    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "\\d{13,19}", message = "Card number must be between 13 and 19 digits")
    private final String cardNumber;

    @NotBlank(message = "Card holder name is required")
    private final String cardHolderName;

    @NotBlank(message = "Expiry date is required")
    @Pattern(regexp = "(0[1-9]|1[0-2])/\\d{2}", message = "Expiry date must be in MM/YY format")
    private final String expiryDate;

    @NotBlank(message = "CVV is required")
    @Pattern(regexp = "\\d{3,4}", message = "CVV must be 3 or 4 digits")
    private final String cvv;
    
    @NotNull(message = "Credit card type is requied")
    private final PaymentType type;
}
