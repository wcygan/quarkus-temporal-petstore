package com.melloware.petstore.order.gateway;

import java.time.Duration;

import jakarta.validation.ConstraintViolationException;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.melloware.petstore.common.activities.order.OrderNotificationActivities;
import com.melloware.petstore.common.activities.order.OrderServiceActivities;
import com.melloware.petstore.common.activities.payment.PaymentActivities;
import com.melloware.petstore.common.activities.shipper.ShipperActivities;
import com.melloware.petstore.common.activities.warehouse.WarehouseActivities;
import com.melloware.petstore.common.models.exceptions.BadPaymentInfoException;
import com.melloware.petstore.common.models.exceptions.OutOfStockException;
import com.melloware.petstore.common.models.exceptions.PaymentDeclinedException;
import com.melloware.petstore.common.models.exceptions.PurchasingException;

import io.quarkus.arc.ArcUndeclaredThrowableException;
import io.temporal.activity.ActivityCancellationType;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;

public class ActivityStubsProvider {

        private final static ActivityOptions options = ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(Duration.ofSeconds(30))
                        .setRetryOptions(RetryOptions.newBuilder()
                                        .setDoNotRetry(
                                                        ArcUndeclaredThrowableException.class.getName(),
                                                        BadPaymentInfoException.class.getName(),
                                                        ConstraintViolationException.class.getName(),
                                                        JsonMappingException.class.getName(),
                                                        NullPointerException.class.getName(),
                                                        OutOfStockException.class.getName(),
                                                        PaymentDeclinedException.class.getName(),
                                                        PurchasingException.class.getName(),
                                                        IllegalArgumentException.class.getName())
                                        .setInitialInterval(Duration.ofSeconds(1))
                                        .setBackoffCoefficient(2)
                                        .build())
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
                                .setTaskQueue("shipment-tasks")
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
                                .setTaskQueue("notification-tasks")
                                .setCancellationType(ActivityCancellationType.WAIT_CANCELLATION_COMPLETED)
                                .build();

                return Workflow.newActivityStub(OrderNotificationActivities.class,
                                newOptions);
        }

}