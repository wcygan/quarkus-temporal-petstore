package com.melloware.petstore.common.models.enums;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Enum representing the different statuses of an order.
 * <p>
 * The enum values represent various states that an order can be in, from its creation to its final processing.
 * Each status provides a brief description of the order's state.
 * </p>
 * 
 
 */
@JsonDeserialize(using = OrderStatus.Deserializer.class)
public enum OrderStatus {
    
    /**
     * Status indicating that the order has been successfully completed.
     */
    COMPLETED("Order was completed"),
    
    /**
     * Status indicating that the order has been cancelled and will not be processed further.
     */
    CANCELLED("Order was cancelled"),
    
    /**
     * Status indicating that the order has been created and is awaiting further processing or completion.
     */
    PENDING("Order is created and awaiting completion"),
    
    /**
     * Status indicating that an error occurred during the processing of the order.
     */
    FAILED("An error occurred during order processing");

    private final String description;

    /**
     * Constructs a new {@code OrderStatus} with the specified description.
     *
     * @param description the description of the status
     */
    OrderStatus(String description) {
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
    static class Deserializer extends CaseInsensitiveEnumDeserializer<OrderStatus> {
        public Deserializer() {
            super(OrderStatus.class);
        }
    }
}