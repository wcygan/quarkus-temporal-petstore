package com.mycompany.order.purchasing.gateway.app.filters;

import java.util.UUID;

import org.jboss.logmanager.MDC;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;
import org.jboss.resteasy.reactive.server.ServerResponseFilter;

import io.quarkus.security.identity.SecurityIdentity;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;

/**
 * Utility class for managing request-related information using JBossLog MDC
 * (Mapped Diagnostic Context). This class provides filters to automatically add
 * request information such as request ID and IP address to the MDC context,
 * making it available for logging purposes throughout the request lifecycle.
 * 
 */
public class RequestIdFilters {

    /**
     * Key for request ID stored in the MDC context.
     */
    public static final String REQUEST_ID_MDC_KEY = "X-Request-Id";

    /**
     * Key for request IP address stored in the MDC context.
     */
    public static final String REQUEST_IP_MDC_KEY = "X-Request-Address";
    
    /**
     * Key for request username store in the MDC context.
     */
    public static final String REQUEST_USER_MDC_KEY = "X-Remote-User";

    /**
     * Injects the Vert.x RoutingContext to access the request object.
     */
    @Inject
    RoutingContext request;
    
    @Inject
    SecurityIdentity securityContext;

    /**
     * Filter method executed before processing the incoming request. Adds
     * request ID and IP address to the MDC context.
     *
     * @param crc ContainerRequestContext provided by JAX-RS runtime.
     */
    @ServerRequestFilter
    public void addRequestInformation(ContainerRequestContext crc) {
        
        // Generate a unique request ID
        String requestId = UUID.randomUUID().toString();
        
        // Retrieve IP address from the incoming request
        String ipAddress = request.request().remoteAddress().hostAddress();

        
        // Get the logged  in user (if any)
        String loggedInUser = extractUsername(securityContext);
        
        // Add request ID and IP address to the MDC context
        MDC.put(REQUEST_ID_MDC_KEY, requestId);
        MDC.put(REQUEST_IP_MDC_KEY, ipAddress);
        MDC.put(REQUEST_USER_MDC_KEY, loggedInUser);
    }

    /**
     * Filter method executed after processing the incoming request. Clears
     * request-related information from the MDC context.
     */
    @ServerResponseFilter
    public void clearRequestInformation() {
        
        // Clear MDC context to avoid memory leaks
        MDC.clear();
    }
    
    /**
     * Retrieves the username from the provided SecurityIdentity.
     * If the username or the security identity itself is null or empty,
     * the method will return the string "ANONYMOUS".
     *
     * @param identity The SecurityIdentity from which to extract the username.
     * @return The extracted username or "ANONYMOUS" if the username
     *         or identity is null or empty.
     */
    public String extractUsername(SecurityIdentity identity) {
        if (identity != null && identity.getPrincipal() != null && identity.getPrincipal().getName() != null) {
            String username = identity.getPrincipal().getName();
            if (!username.trim().isEmpty()) {
                return username;
            }
        }
        return "anonymous";
    }
}
