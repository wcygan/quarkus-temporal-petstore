package com.mycompany.order.purchasing.service;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;

import com.mycompany.order.purchasing.shared.models.json.CreateTrackingNumberRequest;

import lombok.extern.jbosslog.JBossLog;

/**
 * Dummy shipment service to simulate requesting a tracking number from a third
 * party shipping service.
 * 
 */
@ApplicationScoped
@JBossLog
public class ShipperService {

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
