package com.mycompany.order.purchasing.notification.temporal;

import com.mycompany.order.purchasing.notification.service.OrderNotificationService;
import com.mycompany.order.purchasing.shared.activities.order.OrderNotificationActivities;
import com.mycompany.order.purchasing.shared.models.json.OrderErrorEmailNotificationRequest;
import com.mycompany.order.purchasing.shared.models.json.OrderReceivedEmailNotificationRequest;
import com.mycompany.order.purchasing.shared.models.json.OrderSuccessEmailNotificationRequest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * Handles notifications for order operations
 * 
 
 */
@ApplicationScoped
public class OrderNotificationActivitiesImpl implements OrderNotificationActivities {

    @Inject
    OrderNotificationService service;

    @Override
    public void sendOrderReceivedEmail(@Valid @NotNull OrderReceivedEmailNotificationRequest request) {
        service.sendOrderReceivedEmail(request);
    }
    
    @Override
    public void sendOrderErrorEmail(@Valid @NotNull OrderErrorEmailNotificationRequest request) {
        service.sendOrderErrorEmail(request);
    }
    
    @Override
    public void sendOrderSuccessEmail(@Valid @NotNull OrderSuccessEmailNotificationRequest request) {
        service.sendOrderSuccessEmail(request);
    }


}
