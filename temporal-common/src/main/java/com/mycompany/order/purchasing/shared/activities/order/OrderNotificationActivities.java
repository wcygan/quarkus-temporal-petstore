package com.mycompany.order.purchasing.shared.activities.order;

import com.mycompany.order.purchasing.shared.models.json.OrderErrorEmailNotificationRequest;
import com.mycompany.order.purchasing.shared.models.json.OrderReceivedEmailNotificationRequest;
import com.mycompany.order.purchasing.shared.models.json.OrderSuccessEmailNotificationRequest;

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
