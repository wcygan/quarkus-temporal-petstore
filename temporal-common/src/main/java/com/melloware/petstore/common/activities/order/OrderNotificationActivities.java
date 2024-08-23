package com.melloware.petstore.common.activities.order;

import com.melloware.petstore.common.models.json.OrderErrorEmailNotificationRequest;
import com.melloware.petstore.common.models.json.OrderReceivedEmailNotificationRequest;
import com.melloware.petstore.common.models.json.OrderSuccessEmailNotificationRequest;

import io.temporal.activity.ActivityInterface;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * License order notifications
 
 */
@ActivityInterface
public interface OrderNotificationActivities {
    void sendOrderReceivedEmail(@Valid @NotNull OrderReceivedEmailNotificationRequest request);
    void sendOrderSuccessEmail(@Valid @NotNull OrderSuccessEmailNotificationRequest request);
    void sendOrderErrorEmail(@Valid @NotNull OrderErrorEmailNotificationRequest request);
}