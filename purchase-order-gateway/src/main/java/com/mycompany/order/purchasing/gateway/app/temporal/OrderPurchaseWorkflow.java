package com.mycompany.order.purchasing.gateway.app.temporal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import com.mycompany.order.purchasing.gateway.app.OrderPurchaseContext;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Workflow interface for the order purchase workflow
 */
@WorkflowInterface
public interface OrderPurchaseWorkflow {

    @WorkflowMethod(name = "placeOrder")
    void placeOrder(@Valid @NotNull OrderPurchaseContext ctx);
}
