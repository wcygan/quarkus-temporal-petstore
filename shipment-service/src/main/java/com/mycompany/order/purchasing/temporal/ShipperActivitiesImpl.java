package com.mycompany.order.purchasing.temporal;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.mycompany.order.purchasing.service.ShipperService;
import com.mycompany.order.purchasing.shared.activities.shipper.ShipperActivities;
import com.mycompany.order.purchasing.shared.models.json.CreateTrackingNumberRequest;

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
