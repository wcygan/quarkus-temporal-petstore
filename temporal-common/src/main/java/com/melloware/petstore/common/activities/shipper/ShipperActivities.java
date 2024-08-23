package com.melloware.petstore.common.activities.shipper;

import com.melloware.petstore.common.models.json.CreateTrackingNumberRequest;
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