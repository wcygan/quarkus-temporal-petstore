package com.mycompany.order.purchasing.shared.activities.order;

import com.mycompany.order.purchasing.shared.models.json.CreateOrderRequest;
import com.mycompany.order.purchasing.shared.models.json.CreateOrderResponse;
import com.mycompany.order.purchasing.shared.models.json.MarkOrderCompleteRequest;
import com.mycompany.order.purchasing.shared.models.json.MarkOrderFailedRequest;
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
