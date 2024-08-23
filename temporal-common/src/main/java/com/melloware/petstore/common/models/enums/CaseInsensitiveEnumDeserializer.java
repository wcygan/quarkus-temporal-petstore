package com.melloware.petstore.common.models.enums;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.IOException;

/**
 * A generic JSON deserializer for enums that performs case-insensitive matching.
 * <p>
 * This deserializer ensures that enum values are deserialized correctly regardless of the case used in the JSON input. 
 * It converts the input string to uppercase before attempting to match it with enum constants, which allows for 
 * flexibility in the case of the input values.
 * </p>
 *
 * @param <E> The type of the enum to be deserialized.
 */
public class CaseInsensitiveEnumDeserializer<E extends Enum<E>> extends JsonDeserializer<E> {
    private final Class<E> enumClass;

    /**
     * Constructs a {@code CaseInsensitiveEnumDeserializer} for the specified enum class.
     *
     * @param enumClass The class of the enum type to be deserialized.
     */
    protected CaseInsensitiveEnumDeserializer(Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    /**
     * Deserializes a JSON token into an enum value.
     * <p>
     * This method reads the JSON token as a string, converts it to uppercase, and attempts to map it to an enum constant 
     * of the specified type. If the value does not match any enum constants, an exception is thrown.
     * </p>
     *
     * @param jp The JSON parser used to read the JSON content.
     * @param dc The deserialization context.
     * @return The enum constant corresponding to the JSON value.
     * @throws IOException If an I/O error occurs during reading.
     * @throws JsonMappingException If the JSON value cannot be mapped to any enum constant.
     */
    @Override
    public E deserialize(JsonParser jp, DeserializationContext dc) throws IOException {
        String value = jp.getText().toUpperCase(); // Convert the input value to uppercase
        try {
            return Enum.valueOf(enumClass, value); // Attempt to map the value to an enum constant
        } catch (IllegalArgumentException e) {
            throw new JsonMappingException(jp, "Invalid value for enum " + enumClass.getSimpleName() + ": " + value);
        }
    }
}