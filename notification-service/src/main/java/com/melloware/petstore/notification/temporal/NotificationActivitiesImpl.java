package com.melloware.petstore.notification.temporal;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import com.melloware.petstore.common.activities.order.OrderNotificationActivities;
import com.melloware.petstore.common.models.json.OrderErrorEmailNotificationRequest;
import com.melloware.petstore.common.models.json.OrderReceivedEmailNotificationRequest;
import com.melloware.petstore.common.models.json.OrderSuccessEmailNotificationRequest;

/**
 * Handles notifications for order operations
 */
@ApplicationScoped
public class NotificationActivitiesImpl implements OrderNotificationActivities {

    @Inject
    NotificationService service;

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