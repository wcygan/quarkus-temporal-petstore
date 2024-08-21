package com.mycompany.order.purchasing.gateway.app.workflows;

import com.mycompany.order.purchasing.gateway.app.json.OrderPurchaseContext;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * Workflow interface for the order purchase workflow
 */
@WorkflowInterface
public interface OrderPurchaseWorkflow {
    
    @WorkflowMethod(name = "placeOrder")
    void placeOrder(@Valid @NotNull OrderPurchaseContext ctx);
}
