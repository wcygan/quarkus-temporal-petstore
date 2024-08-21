package com.mycompany.order.purchasing.shared.utils;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.MDC;

import io.temporal.api.common.v1.Payload;
import io.temporal.common.context.ContextPropagator;
import io.temporal.common.converter.GlobalDataConverter;

/**
 * A {@link ContextPropagator} implementation that propagates the SLF4J MDC (Mapped Diagnostic Context)
 * across Temporal workflow and activity boundaries. This class ensures that MDC entries with keys
 * starting with "X-" are propagated.
 * 
 * <p>Usage:
 * <pre>
 * {@code
 * MDCContextPropagator contextPropagator = new MDCContextPropagator();
 * }
 * </pre>
 * 
 
 */
public class MDCContextPropagator implements ContextPropagator {

    /**
     * Gets the name of the context propagator.
     *
     * @return the name of the context propagator, which is the class name.
     */
    @Override
    public String getName() {
        return this.getClass().getName();
    }

    /**
     * Gets the current MDC context to be propagated.
     *
     * @return a map containing the current MDC context, filtered to include only entries with keys
     *         starting with "X-".
     */
    @Override
    public Object getCurrentContext() {
        Map<String, String> context = new HashMap<>();
        if (MDC.getCopyOfContextMap() == null) {
            return context;
        }
        for (Map.Entry<String, String> entry : MDC.getCopyOfContextMap().entrySet()) {
            if (entry.getKey().startsWith("X-")) {
                context.put(entry.getKey(), entry.getValue());
            }
        }
        return context;
    }

    /**
     * Sets the current MDC context from the given context map.
     *
     * @param context the context map containing MDC entries to be set.
     */
    @Override
    public void setCurrentContext(Object context) {
        Map<String, String> contextMap = (Map<String, String>) context;
        for (Map.Entry<String, String> entry : contextMap.entrySet()) {
            MDC.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Serializes the given context map to a map of Payloads.
     *
     * @param context the context map containing MDC entries to be serialized.
     * @return a map of Payloads representing the serialized context.
     */
    @Override
    public Map<String, Payload> serializeContext(Object context) {
        Map<String, String> contextMap = (Map<String, String>) context;
        Map<String, Payload> serializedContext = new HashMap<>();
        for (Map.Entry<String, String> entry : contextMap.entrySet()) {
            serializedContext.put(
                    entry.getKey(), GlobalDataConverter.get().toPayload(entry.getValue()).get());
        }
        return serializedContext;
    }

    /**
     * Deserializes the given map of Payloads to a context map.
     *
     * @param context the map of Payloads to be deserialized.
     * @return a context map containing the deserialized MDC entries.
     */
    @Override
    public Object deserializeContext(Map<String, Payload> context) {
        Map<String, String> contextMap = new HashMap<>();
        for (Map.Entry<String, Payload> entry : context.entrySet()) {
            contextMap.put(
                    entry.getKey(),
                    GlobalDataConverter.get()
                            .fromPayload(entry.getValue(), String.class, String.class));
        }
        return contextMap;
    }
}
