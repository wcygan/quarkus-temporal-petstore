package com.mycompany.order.purchasing.service;

import java.util.UUID;

import org.jboss.logging.Logger;

import com.mycompany.order.purchasing.shared.models.json.CreateTrackingNumberRequest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Dummy shipper service
 * 
 
 */
@ApplicationScoped
public class ShipperService {

    @Inject
    Logger log;

    /**
     * Create a tracking number
     * 
     * @param request {@link CreateTrackingNumberRequest}
     * @return Tracking number string
     */
    public String createTrackingNumber(CreateTrackingNumberRequest request) {
        log.infof("Generating new tracking number for %d products", request.getProducts().size());
        
        String tracker = UUID.randomUUID().toString();
        
        log.infof("Generated tracking number %s", tracker);
        return tracker;
        
    }
}
