package com.mycompany.order.purchasing.shared.activities.shipper;

import com.mycompany.order.purchasing.shared.models.json.CreateTrackingNumberRequest;
import io.temporal.activity.ActivityInterface;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 *
 
 */
@ActivityInterface
public interface ShipperActivities {
    String createTrackingNumber(@Valid @NotNull CreateTrackingNumberRequest request);
}
