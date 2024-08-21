package com.mycompany.order.purchasing.shared.models.json;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 *
 
 */
@SuperBuilder
@Getter
@ToString
@Jacksonized
public class ReverseActionsForTransactionRequest extends RequiredFieldsBaseBuilder {
    
    @NotNull
    private final UUID transactionId;
    
}
