package com.mycompany.order.purchasing.shared.activities.payment;

import com.mycompany.order.purchasing.shared.models.json.DebitCreditCardRequest;
import com.mycompany.order.purchasing.shared.models.json.DebitCreditCardResponse;
import com.mycompany.order.purchasing.shared.models.json.ReverseActionsForTransactionRequest;

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
