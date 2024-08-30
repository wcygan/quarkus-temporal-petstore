package com.melloware.petstore.order.gateway.temporal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Workflow interface for the order purchase workflow
 */
@WorkflowInterface
public interface PurchaseOrderWorkflow {

    @WorkflowMethod(name = "placeOrder")
    void placeOrder(@Valid @NotNull PurchaseOrderContext ctx);
}