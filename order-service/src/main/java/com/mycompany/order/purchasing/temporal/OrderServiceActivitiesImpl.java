package com.mycompany.order.purchasing.temporal;

import com.mycompany.order.purchasing.service.OrderService;
import com.mycompany.order.purchasing.shared.activities.order.OrderServiceActivities;
import com.mycompany.order.purchasing.shared.models.json.CreateOrderRequest;
import com.mycompany.order.purchasing.shared.models.json.CreateOrderResponse;
import com.mycompany.order.purchasing.shared.models.json.MarkOrderCompleteRequest;
import com.mycompany.order.purchasing.shared.models.json.MarkOrderFailedRequest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Implementation of the GiftCard Activity
 *
 
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
