package com.mycompany.order.purchasing.gateway.app;

import java.time.Duration;
import java.util.List;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.mycompany.order.purchasing.shared.activities.order.OrderNotificationActivities;
import com.mycompany.order.purchasing.shared.activities.order.OrderServiceActivities;
import com.mycompany.order.purchasing.shared.activities.payment.PaymentActivities;
import com.mycompany.order.purchasing.shared.activities.shipper.ShipperActivities;
import com.mycompany.order.purchasing.shared.activities.warehouse.WarehouseActivities;
import com.mycompany.order.purchasing.shared.models.exceptions.BadPaymentInfoException;
import com.mycompany.order.purchasing.shared.models.exceptions.OutOfStockException;
import com.mycompany.order.purchasing.shared.models.exceptions.PaymentDeclinedException;
import com.mycompany.order.purchasing.shared.models.exceptions.PurchasingException;
import com.mycompany.order.purchasing.shared.utils.MDCContextPropagator;

import io.quarkus.arc.ArcUndeclaredThrowableException;
import io.temporal.activity.ActivityCancellationType;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import jakarta.validation.ConstraintViolationException;

public class ActivityStubsProvider {

    private final static ActivityOptions options = ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(30))
            .setRetryOptions(RetryOptions.newBuilder()
                    .setDoNotRetry(IllegalArgumentException.class.getName(),
                            PaymentDeclinedException.class.getName(),
                            BadPaymentInfoException.class.getName(),
                            PurchasingException.class.getName(),
                            JsonMappingException.class.getName(),
                            ArcUndeclaredThrowableException.class.getName(),
                            ConstraintViolationException.class.getName(),
                            NullPointerException.class.getName(),
                            OutOfStockException.class.getName(),
                            BadPaymentInfoException.class.getName(),
                            PaymentDeclinedException.class.getName()
                            )
                    .setInitialInterval(Duration.ofSeconds(1))
                    .setBackoffCoefficient(2)
                    .build())
            .setContextPropagators(List.of(new MDCContextPropagator()))
            .build();

    private ActivityStubsProvider() {
    }

    // we use setScheduleToCloseTimeout for the demo
    // in order to limit the activity retry time
    // this is done so we don't have to wait too long in demo to show failure
    public static WarehouseActivities getWarehouseActivities() {
        ActivityOptions newOptions = ActivityOptions.newBuilder(options)
                .setTaskQueue("warehouse-tasks")
                .setCancellationType(ActivityCancellationType.WAIT_CANCELLATION_COMPLETED)
                .build();

        return Workflow.newActivityStub(
                WarehouseActivities.class,
                newOptions);
    }

    
    // we use setScheduleToCloseTimeout for the demo
    // in order to limit the activity retry time
    // this is done so we don't have to wait too long in demo to show failure
    public static ShipperActivities getShipperActivities() {
        ActivityOptions newOptions = ActivityOptions.newBuilder(options)
                .setTaskQueue("shipper-tasks")
                .setCancellationType(ActivityCancellationType.WAIT_CANCELLATION_COMPLETED)
                .build();

        return Workflow.newActivityStub(
                ShipperActivities.class,
                newOptions);
    }

    public static PaymentActivities getPaymentActivities() {
        ActivityOptions newOptions = ActivityOptions.newBuilder(options)
                .setTaskQueue("payment-tasks")
                .setCancellationType(ActivityCancellationType.WAIT_CANCELLATION_COMPLETED)
                .build();

        return Workflow.newActivityStub(PaymentActivities.class,
                newOptions);
    }

    public static OrderServiceActivities getOrderServiceActivities() {
        ActivityOptions newOptions = ActivityOptions.newBuilder(options)
                .setTaskQueue("order-tasks")
                .setCancellationType(ActivityCancellationType.WAIT_CANCELLATION_COMPLETED)
                .build();

        return Workflow.newActivityStub(OrderServiceActivities.class,
                newOptions);
    }

    public static OrderNotificationActivities getOrderNotificationActivities() {
        ActivityOptions newOptions = ActivityOptions.newBuilder(options)
                .setTaskQueue("order-notification-tasks")
                .setCancellationType(ActivityCancellationType.WAIT_CANCELLATION_COMPLETED)
                .build();

        return Workflow.newActivityStub(OrderNotificationActivities.class,
                newOptions);
    }

    
}
