package com.melloware.petstore.common.activities.order;

import com.melloware.petstore.common.models.json.CreateOrderRequest;
import com.melloware.petstore.common.models.json.CreateOrderResponse;
import com.melloware.petstore.common.models.json.MarkOrderCompleteRequest;
import com.melloware.petstore.common.models.json.MarkOrderFailedRequest;
import io.temporal.activity.ActivityInterface;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * Operations performed from the Temporal workflow
 * <br>
 * This can be running locally or on another machine
 * 
 
 */
@ActivityInterface
public interface OrderServiceActivities {
    CreateOrderResponse createOrder(@Valid @NotNull CreateOrderRequest request);
    void markOrderAsComplete(@Valid @NotNull MarkOrderCompleteRequest request);
    void markOrderAsFailed(@Valid @NotNull MarkOrderFailedRequest request);
    
}