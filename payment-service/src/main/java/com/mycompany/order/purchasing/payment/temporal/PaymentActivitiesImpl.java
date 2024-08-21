package com.mycompany.order.purchasing.payment.temporal;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import com.mycompany.order.purchasing.payment.service.PaymentService;
import com.mycompany.order.purchasing.shared.activities.payment.PaymentActivities;
import com.mycompany.order.purchasing.shared.models.json.DebitCreditCardRequest;
import com.mycompany.order.purchasing.shared.models.json.DebitCreditCardResponse;
import com.mycompany.order.purchasing.shared.models.json.ReverseActionsForTransactionRequest;

/**
 * Implementation of the Payment Activity.
 */
@ApplicationScoped
public class PaymentActivitiesImpl implements PaymentActivities {

    @Inject
    PaymentService service;

    /**
     * Attempt to debit the credit card
     *
     * @param request {@link DebitCreditCardRequest}
     * @return {@link DebitCreditCardResponse}
     */
    @Override
    public DebitCreditCardResponse debitCreditCard(@Valid @NotNull DebitCreditCardRequest request) {
        return service.debitAccount(request);
    }

    /**
     * Reverse any transactions for the given request and card IDs
     *
     * @param request {@link ReverseActionsForTransactionRequest}
     */
    @Override
    public void reversePaymentTransactions(@Valid @NotNull ReverseActionsForTransactionRequest request) {
        service.reverseTransactions(request);
    }

}
