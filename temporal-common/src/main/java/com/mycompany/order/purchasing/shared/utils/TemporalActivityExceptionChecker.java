package com.mycompany.order.purchasing.shared.utils;

import java.util.Objects;

import io.temporal.failure.ApplicationFailure;

/**
 * Utility class for checking if a given exception is of a specific type
 * when working with Temporal activities.
 * 
 * Temporal activities can throw {@link ApplicationFailure} exceptions,
 * which contain information about the original exception type. This utility
 * helps in determining if the cause of an exception matches a specified class.
 * 
 
 */
public class TemporalActivityExceptionChecker {

    /**
     * Checks if the given exception's cause is an {@link ApplicationFailure}
     * and if the type of that failure matches the specified exception class.
     * 
     * @param e the exception to check
     * @param exceptionClass the class of the exception type to match against
     * @return {@code true} if the cause of the exception is an {@link ApplicationFailure}
     *         and its type matches the specified exception class; {@code false} otherwise
     */
    public static boolean isExceptionType(Exception e, Class<?> exceptionClass) {
        Objects.requireNonNull(e, "Exception is required");
        Objects.requireNonNull(exceptionClass, "Exception class is required");
        
        if (e.getCause() instanceof ApplicationFailure) {
            String exceptionType = ((ApplicationFailure) (e.getCause())).getType();
            return exceptionType.equals(exceptionClass.getName());
        }
        return false;
    }
}
