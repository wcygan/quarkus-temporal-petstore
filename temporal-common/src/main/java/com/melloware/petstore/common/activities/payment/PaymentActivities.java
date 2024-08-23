package com.melloware.petstore.common.activities.payment;

import com.melloware.petstore.common.models.json.DebitCreditCardRequest;
import com.melloware.petstore.common.models.json.DebitCreditCardResponse;
import com.melloware.petstore.common.models.json.ReverseActionsForTransactionRequest;

import io.temporal.activity.ActivityInterface;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * Activity interface for a Payment service which processes Credit cards
 * 
 
 */
@ActivityInterface
public interface PaymentActivities {
    
    /**
     * Charge a credit card
     * 
     * @param request {@link DebitCreditCardRequest}
     * @return {@link DebitCreditCardResponse}
     */
    DebitCreditCardResponse debitCreditCard(@Valid @NotNull DebitCreditCardRequest request);
    
    /**
     * Reverse any transactions for the given transaction id
     * 
     * @param request {@ReverseActionsForTransactionRequest}
     */
    void reversePaymentTransactions(@Valid @NotNull ReverseActionsForTransactionRequest request);
}