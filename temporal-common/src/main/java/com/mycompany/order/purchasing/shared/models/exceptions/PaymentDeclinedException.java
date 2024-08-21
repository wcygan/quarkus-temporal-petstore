package com.mycompany.order.purchasing.shared.models.exceptions;

/**
 * Exception when a Conflict is detected in the application
 *
 
 */
public class PaymentDeclinedException extends RuntimeException {

    /**
     * Default
     */
    public PaymentDeclinedException() {
        super();
    }

    /**
     * Takes the message
     *
     * @param message
     */
    public PaymentDeclinedException(String message) {
        super(message);
    }

    /**
     * Takes a message and cause
     *
     * @param message
     * @param cause
     */
    public PaymentDeclinedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Takes the cause
     *
     * @param cause
     */
    public PaymentDeclinedException(Throwable cause) {
        super(cause);
    }

}
