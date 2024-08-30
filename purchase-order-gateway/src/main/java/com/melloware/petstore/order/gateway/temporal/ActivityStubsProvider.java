package com.melloware.petstore.order.gateway.temporal;

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

import lombok.experimental.UtilityClass;

/**
 * Provider class for creating activity stubs with predefined options.
 * This class sets up common activity options and provides methods to create
 * activity stubs for various services in the pet store application.
 */
@UtilityClass
public class ActivityStubsProvider {

        // Activity options for demo purposes, limiting retry time
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
                                        .setMaximumInterval(Duration.ofSeconds(100))
                                        .setBackoffCoefficient(2)
                                        .setMaximumAttempts(500)
                                        .build())
                        .build();

        /**
         * Creates and returns a WarehouseActivities stub.
         *
         * @return WarehouseActivities stub with predefined options
         */
        public static WarehouseActivities getWarehouseActivities() {
                ActivityOptions newOptions = ActivityOptions.newBuilder(options)
                                .setTaskQueue("warehouse-tasks")
                                .setCancellationType(ActivityCancellationType.WAIT_CANCELLATION_COMPLETED)
                                .build();

                return Workflow.newActivityStub(
                                WarehouseActivities.class,
                                newOptions);
        }

        /**
         * Creates and returns a ShipperActivities stub.
         *
         * @return ShipperActivities stub with predefined options
         */
        public static ShipperActivities getShipperActivities() {
                ActivityOptions newOptions = ActivityOptions.newBuilder(options)
                                .setTaskQueue("shipment-tasks")
                                .setCancellationType(ActivityCancellationType.WAIT_CANCELLATION_COMPLETED)
                                .build();

                return Workflow.newActivityStub(
                                ShipperActivities.class,
                                newOptions);
        }

        /**
         * Creates and returns a PaymentActivities stub.
         *
         * @return PaymentActivities stub with predefined options
         */
        public static PaymentActivities getPaymentActivities() {
                ActivityOptions newOptions = ActivityOptions.newBuilder(options)
                                .setTaskQueue("payment-tasks")
                                .setCancellationType(ActivityCancellationType.WAIT_CANCELLATION_COMPLETED)
                                .build();

                return Workflow.newActivityStub(PaymentActivities.class,
                                newOptions);
        }

        /**
         * Creates and returns an OrderServiceActivities stub.
         *
         * @return OrderServiceActivities stub with predefined options
         */
        public static OrderServiceActivities getOrderServiceActivities() {
                ActivityOptions newOptions = ActivityOptions.newBuilder(options)
                                .setTaskQueue("order-tasks")
                                .setCancellationType(ActivityCancellationType.WAIT_CANCELLATION_COMPLETED)
                                .build();

                return Workflow.newActivityStub(OrderServiceActivities.class,
                                newOptions);
        }

        /**
         * Creates and returns an OrderNotificationActivities stub.
         *
         * @return OrderNotificationActivities stub with predefined options
         */
        public static OrderNotificationActivities getOrderNotificationActivities() {
                ActivityOptions newOptions = ActivityOptions.newBuilder(options)
                                .setTaskQueue("notification-tasks")
                                .setCancellationType(ActivityCancellationType.WAIT_CANCELLATION_COMPLETED)
                                .build();

                return Workflow.newActivityStub(OrderNotificationActivities.class,
                                newOptions);
        }

}