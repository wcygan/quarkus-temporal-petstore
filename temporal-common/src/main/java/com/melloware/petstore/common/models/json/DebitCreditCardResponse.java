package com.melloware.petstore.common.models.json;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

/**
 * Response from charging a customers credit card
 * 
 
 */
@Builder
@Getter
@ToString
@Jacksonized
public class DebitCreditCardResponse {

    /**
     * Authorization code
     */
    @NotNull
    private final UUID authorizationCode;

    /**
     * Amount charged
     */
    @PositiveOrZero
    private final double chargedAmount;

    /**
     * Original Credit card information
     */
    @NotNull
    @Valid
    private final CreditCardInfo cardInfo;
}