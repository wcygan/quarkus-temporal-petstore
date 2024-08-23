package com.melloware.petstore.common.models.enums;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Enumeration of possible failure reasons for product orders.
 */
@JsonDeserialize(using = OrderFailureReason.Deserializer.class)
public enum OrderFailureReason {
    NONE("None"),
    INSUFFICIENT_FUNDS("Insufficient funds on one or more payment methods"),
    INVALID_PAYMENT_METHOD("Invalid payment supplied"),
    SYSTEM_ERROR("System error occurred during processing"),
    OUT_OF_STOCK_ITEMS("One or more items were out of stock"),
    PAYMENT_DECLINED("Payment was declined"),
    UNKNOWN("Unknown error");

    private final String description;

     /**
     * Constructs a new {@code LicenseOrderFailureReason} with the specified description.
     *
     * @param description the description of the status
     */
    OrderFailureReason(String description) {
        this.description = description;
    }

    /**
     * Returns the description of the status.
     *
     * @return the description of the status
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the string representation of the status, including its name and description.
     *
     * @return the string representation of the status
     */
    @Override
    public String toString() {
        return this.name() + ": " + this.getDescription();
    }
    
     /**
     * Deserializer for {@code OrderStatus} that handles case-insensitive JSON parsing.
     * <p>
     * This deserializer allows the enum values to be parsed regardless of the case used in the JSON input,
     * ensuring flexibility and robustness in handling different input formats.
     * </p>
     */
    static class Deserializer extends CaseInsensitiveEnumDeserializer<OrderFailureReason> {
        public Deserializer() {
            super(OrderFailureReason.class);
        }
    }
}