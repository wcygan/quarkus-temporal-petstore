package com.melloware.petstore.common.models.json;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.melloware.petstore.common.models.enums.PaymentType;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Builder
@Getter
@ToString
@Jacksonized
@Schema(description = "Credit card information for payment processing")
public class CreditCardInfo {

    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "\\d{13,19}", message = "Card number must be between 13 and 19 digits")
    @Schema(description = "Credit card number", example = "4400123487650987")
    private final String cardNumber;

    @NotBlank(message = "Card holder name is required")
    @Schema(description = "Name of the card holder", example = "Homer Simpson")
    private final String cardHolderName;

    @NotBlank(message = "Expiry date is required")
    @Pattern(regexp = "(0[1-9]|1[0-2])/\\d{2}", message = "Expiry date must be in MM/YY format")
    @Schema(description = "Expiry date of the card in MM/YY format", example = "12/25")
    private final String expiryDate;

    @NotBlank(message = "CVV is required")
    @Pattern(regexp = "\\d{3,4}", message = "CVV must be 3 or 4 digits")
    @Schema(description = "Card Verification Value (CVV)", example = "372")
    private final String cvv;

    @NotNull(message = "Credit card type is required")
    @Schema(description = "Type of the credit card", example = "VISA")
    private final PaymentType type;
}