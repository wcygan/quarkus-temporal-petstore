package com.melloware.petstore.shipment.temporal;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.melloware.petstore.common.activities.shipper.ShipperActivities;
import com.melloware.petstore.common.models.json.CreateTrackingNumberRequest;

/**
 * Implementation of the Shipper Activity.
 */
@ApplicationScoped
public class ShipperActivitiesImpl implements ShipperActivities {

    @Inject
    ShipperService service;

    /**
     * Create a tracking number for the given request
     * 
     * @param request {@link CreateTrackingNumberRequest}
     * @return Tracking number
     */
    @Override
    public String createTrackingNumber(CreateTrackingNumberRequest request) {
        return service.createTrackingNumber(request);
    }

}