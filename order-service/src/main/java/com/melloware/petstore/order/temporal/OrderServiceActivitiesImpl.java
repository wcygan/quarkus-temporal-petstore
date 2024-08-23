package com.melloware.petstore.order.temporal;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.melloware.petstore.common.activities.order.OrderServiceActivities;
import com.melloware.petstore.common.models.json.CreateOrderRequest;
import com.melloware.petstore.common.models.json.CreateOrderResponse;
import com.melloware.petstore.common.models.json.MarkOrderCompleteRequest;
import com.melloware.petstore.common.models.json.MarkOrderFailedRequest;

/**
 * Implementation of the GiftCard Activity.
 */
@ApplicationScoped
public class OrderServiceActivitiesImpl implements OrderServiceActivities {

    @Inject
    OrderService service;

    @Override
    public void markOrderAsFailed(MarkOrderFailedRequest request) {
        service.markOrderAsFailed(request);
    }

    @Override
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        return service.createOrder(request);
    }

    @Override
    public void markOrderAsComplete(MarkOrderCompleteRequest request) {
        service.markOrderAsComplete(request);
    }
}