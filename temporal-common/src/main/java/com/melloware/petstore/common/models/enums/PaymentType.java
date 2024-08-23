package com.melloware.petstore.common.models.enums;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Enum representing different types of credit cards
 * <p>
 * This enum defines various types of payment codes available in the system. Each type has a descriptive name
 * to identify the version of the payment code
 * </p>
 * 
 
 */
@JsonDeserialize(using = PaymentType.Deserializer.class)
public enum PaymentType {
    
    /**
     * Represents the Credit card
     */
    VISA("Visa"),
    MASTERCARD("Master Card"),
    AMEX("American Express"),
    DISCOVER("Discover Card");
    
    private final String description;

    /**
     * Constructs a new {@code PaymentCodeType} with the specified description.
     *
     * @param description a brief description of the payment code  type
     */
    PaymentType(String description) {
        this.description = description;
    }

    /**
     * Returns the description of the payment code  type.
     *
     * @return the description of the payment code  type
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the string representation of the payment code  type, including its name and description.
     *
     * @return the string representation of the payment code  type
     */
    @Override
    public String toString() {
        return this.name() + ": " + this.getDescription();
    }
    
    /**
     * Deserializer for {@code PaymentCodeType} that handles case-insensitive JSON parsing.
     * <p>
     * This deserializer ensures that the enum values can be parsed from JSON regardless of the case used
     * in the input, providing flexibility in handling different input formats.
     * </p>
     */
    static class Deserializer extends CaseInsensitiveEnumDeserializer<PaymentType> {
        public Deserializer() {
            super(PaymentType.class);
        }
    }
}