package com.melloware.petstore.common.models.json;

import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * Request object to charge a credit card
 * 
 
 */
@SuperBuilder
@Getter
@ToString
@Jacksonized
public class DebitCreditCardRequest extends RequiredFieldsBaseBuilder {
    
    /**
     * Transaction ID which should be the correlation id used across
     * the whole system for auditing purposes
     */
    @NotNull
    private final UUID transactionId;
    
    /**
     * Credit card information
     */
    @Schema(description = "UUID of the code being requested to debit", required = true)
    @NotNull
    private final CreditCardInfo creditCard;
    
    /**
     * The amount to charge the card
     */
    @Schema(description = "Amount to charge credit card", required = true)
    private final double amount;
    
    /**
     * The customers email which could be used to verify the card belongs to the
     * user, etc. You can remove this if you don't need it
     */
    @Schema(description = "Email address of the card owner", required=true)
    @NotBlank
    private final String customerEmail;
    
}