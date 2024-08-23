package com.melloware.petstore.payment.service;

import java.util.Objects;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import com.melloware.petstore.common.models.exceptions.BadPaymentInfoException;
import com.melloware.petstore.common.models.exceptions.PaymentDeclinedException;
import com.melloware.petstore.common.models.json.DebitCreditCardRequest;
import com.melloware.petstore.common.models.json.DebitCreditCardResponse;
import com.melloware.petstore.common.models.json.ReverseActionsForTransactionRequest;

import lombok.extern.jbosslog.JBossLog;

/**
 * Service class which handles credit card processing.
 */
@ApplicationScoped
@JBossLog
public class PaymentService {

    /**
     * Attempts to reverse a previous transaction for all codes associated with
     * that transaction id
     *
     * @param request {@link ReverseActionsForTransactionRequest}
     */
    public void reverseTransactions(@Valid @NotNull ReverseActionsForTransactionRequest request) {
        Objects.requireNonNull(request, "ReverseAccountsTransactionRequest instance required");

        log.infof("Attempting compensations for all transactions with TX id %s", request.getTransactionId());

        /**
         * This is where you'd perform your compensation logic
         *
         * In my real world example I simply just reversed everything in the
         * database that matched the incoming Transaction ID
         * 
         * But maybe you'd call your credit card 3rd party api to reverse the charge
         */
        log.infof("Compensation completed for all transactions with TX id %s", request.getTransactionId());
    }

    /**
     * Debits a credit card account
     *
     * @param request {@link DebitCreditCardRequest}
     * @return {@link DebitCreditCardResponse}
     */
    public DebitCreditCardResponse debitAccount(@Valid @NotNull DebitCreditCardRequest request) {

        Objects.requireNonNull(request, "DebitCreditCardRequest instance required");
        log.infof("Attempting to debit %.2f from credit card %s", request.getAmount(),
                request.getCreditCard().getCardNumber());

        /**
         * This is where you'd perform the charging of the credit card and then
         * return some response information.
         * 
         * In this instance I just return an authorization code, the charged amount, and
         * original
         * credit card information.
         */

        /**
         * For demo, you could throw a bad payment info if the email isn't valid
         * and another error for any orders over 1000 dollars, etc.
         */
        final int creditLimit = 1000;
        if (request.getCustomerEmail().equalsIgnoreCase("bad_customer@foo.com")) {
            throw new BadPaymentInfoException("Customer email doesn't match card owner");
        } else if (request.getAmount() > creditLimit) {
            throw new PaymentDeclinedException("Order amount "+request.getAmount()+" exceeds credit limit of " + creditLimit);
        }

        // Return the response
        return DebitCreditCardResponse.builder()
                .authorizationCode(UUID.randomUUID())
                .cardInfo(request.getCreditCard())
                .chargedAmount(request.getAmount())
                .build();

    }

}