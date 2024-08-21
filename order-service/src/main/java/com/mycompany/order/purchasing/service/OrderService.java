package com.mycompany.order.purchasing.service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;

import com.mycompany.order.purchasing.entity.OrderEntity;
import com.mycompany.order.purchasing.entity.OrderLineItemEntity;
import com.mycompany.order.purchasing.repository.OrderRepository;
import com.mycompany.order.purchasing.shared.models.enums.OrderStatus;
import com.mycompany.order.purchasing.shared.models.json.CreateOrderRequest;
import com.mycompany.order.purchasing.shared.models.json.CreateOrderResponse;
import com.mycompany.order.purchasing.shared.models.json.MarkOrderCompleteRequest;
import com.mycompany.order.purchasing.shared.models.json.MarkOrderFailedRequest;

import lombok.extern.jbosslog.JBossLog;

/**
 * Service class for managing order-related operations.
 * This class handles creating orders, marking orders as complete or failed,
 * and other order-related business logic.
 */
@ApplicationScoped
@JBossLog
public class OrderService {

    @Inject
    OrderRepository orderRepo;

    /**
     * Marks an order as complete.
     *
     * @param ctx The request containing information to mark the order as complete.
     * @throws IllegalArgumentException if the order is not found or if there's an
     *                                  error saving the record.
     */
    @Transactional
    public void markOrderAsComplete(MarkOrderCompleteRequest ctx) {
        log.infof("Attempting to save order %s with TX id %s", ctx.getOrderNumber(),
                ctx.getTransactionId());

        OrderEntity record = orderRepo.find("transactionId = ?1 and orderNumber = ?2",
                ctx.getTransactionId(), ctx.getOrderNumber())
                .withLock(LockModeType.PESSIMISTIC_WRITE)
                .firstResult();

        if (record == null) {
            throw new IllegalArgumentException(
                    "Previous order number %s was not found".formatted(ctx.getOrderNumber()));
        }

        // Set order total
        record.setOrderTotal(ctx.getOrderTotal());

        // Build line items
        Set<OrderLineItemEntity> lineItems = ctx.getProducts().stream()
                .map(p -> {
                    OrderLineItemEntity lineItem = new OrderLineItemEntity();
                    lineItem.setProductSku(p.getSku());
                    lineItem.setQuantity(p.getQuantity());
                    lineItem.setUnitPrice(p.getPrice());
                    return lineItem;
                }).collect(Collectors.toSet());

        // Set the status
        record.setStatus(OrderStatus.COMPLETED);

        // Clear existing line items and add new ones
        record.getLineItems().clear();
        record.getLineItems().addAll(lineItems);

        try {
            orderRepo.saveOrUpdate(record);
            log.infof("Successfully saved order %s with TX id %s", ctx.getOrderNumber(),
                    ctx.getTransactionId());
        } catch (Exception e) {
            log.errorf(e, "Error saving record - %s", record);
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Creates a new order.
     *
     * @param request The request containing information to create a new order.
     * @return A CreateOrderResponse containing the details of the created order.
     * @throws IllegalArgumentException if an order with the same transaction ID
     *                                  already exists and is not in PENDING state.
     */
    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request) {

        // Verify we didn't already try to create an order with the transaction id
        // This is because the workflow could get replayed and we need to make sure
        // we are idempotent
        OrderEntity previousOrder = orderRepo.find("transactionId = ?1",
                request.getTransactionId()).firstResult();

        // if we are still in PENDING state, something happened and we didn't finish the
        // order
        // and we are being asked to attempt to finish it (Workflow Replay)
        // otherwise we fail because the order has been completed either successfully or
        // failed
        if (previousOrder != null) {
            if (previousOrder.getStatus() == OrderStatus.PENDING) {
                log.warnf("Previous transaction found for order number %s with TX id %s...returning previous order"
                        .formatted(previousOrder.getOrderNumber(), previousOrder.getTransactionId()));

                return CreateOrderResponse.builder()
                        .transactionId(previousOrder.getTransactionId())
                        .orderDate(previousOrder.getOrderDate())
                        .customerEmail(previousOrder.getCustomerEmail())
                        .orderNumber(previousOrder.getOrderNumber())
                        .status(previousOrder.getStatus())
                        .build();
            } else {
                throw new IllegalArgumentException(
                        "Attempted to create a new order with existing order number %s and transaction %s that has been previously completed"
                                .formatted(previousOrder.getOrderNumber(), previousOrder.getTransactionId()));
            }
        }

        // Create new order since we didn't find any previous attempts
        OrderEntity record = new OrderEntity();
        record.setCustomerEmail(request.getCustomerEmail());
        record.setOrderDate(request.getOrderDate());
        record.setOrderNumber(generateOrderNumber());
        record.setTransactionId(request.getTransactionId());
        record.setStatus(OrderStatus.PENDING);
        record.setRequestedByHost(request.getRequestedByHost());
        record.setRequestedByUser(request.getRequestedByUser());
        orderRepo.saveOrUpdate(record);

        log.infof("Created new order %s with TX id %s", record.getOrderNumber(), record.getTransactionId());
        return CreateOrderResponse.builder()
                .transactionId(record.getTransactionId())
                .orderDate(record.getOrderDate())
                .customerEmail(record.getCustomerEmail())
                .orderNumber(record.getOrderNumber())
                .status(record.getStatus())
                .build();
    }

    /**
     * Marks an order as failed.
     *
     * @param request The request containing information to mark the order as
     *                failed.
     * @throws IllegalArgumentException if the order is not found.
     */
    @Transactional
    public void markOrderAsFailed(MarkOrderFailedRequest request) {

        String orderNumber = request.getOrderNumber();
        Optional<OrderEntity> optionalRecord;

        // If we don't have an order number
        // We search by transaction id
        // This can happen if the order failed in the process before we have an order id
        if (orderNumber == null || orderNumber.isBlank()) {
            log.errorf("Marking order as FAILED with TX id %s", request.getTransactionId());
            optionalRecord = orderRepo.findByTransactionId(request.getTransactionId());
        } else {
            log.errorf("Marking order %s as FAILED with TX id %s", request.getOrderNumber(),
                    request.getTransactionId());
            optionalRecord = orderRepo.findByOrderNumber(request.getOrderNumber());
        }

        OrderEntity record = optionalRecord
                .orElseThrow(() -> new IllegalArgumentException(
                        "Previous order for TX %s was not found".formatted(request.getTransactionId())));

        record.setStatus(OrderStatus.FAILED);
        record.setFailureReason(request.getReason());
    }

    /**
     * Generates a new order number.
     *
     * @return A string representing the newly generated order number.
     */
    private String generateOrderNumber() {

        // Generate a UUID
        UUID uuid = UUID.randomUUID();

        // Convert UUID to a string and remove the hyphens
        String uuidString = uuid.toString().replace("-", "");

        // Extract the first 16 hexadecimal digits
        String first16HexDigits = uuidString.substring(0, 16);

        // Format the string to match RJT-xxxxxxxx-xxxx-xxxx
        String formattedOrderNumber = String.format("MYC-ORD-%s-%s-%s",
                first16HexDigits.substring(0, 8),
                first16HexDigits.substring(8, 12),
                first16HexDigits.substring(12, 16));

        log.infof("Generated a new order number: %s", formattedOrderNumber);
        return formattedOrderNumber;
    }
}
