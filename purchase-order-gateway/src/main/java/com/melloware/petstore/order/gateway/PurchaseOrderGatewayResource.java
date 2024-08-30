package com.melloware.petstore.order.gateway;

import java.time.ZonedDateTime;
import java.util.UUID;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logmanager.MDC;

import com.melloware.petstore.common.models.json.OrderPurchaseRequest;
import com.melloware.petstore.common.models.json.WorkflowInitiationResponse;
import com.melloware.petstore.order.gateway.filters.RequestIdFilters;
import com.melloware.petstore.order.gateway.temporal.PurchaseOrderContext;
import com.melloware.petstore.order.gateway.temporal.PurchaseOrderWorkflow;

import io.micrometer.core.annotation.Timed;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;

import lombok.extern.jbosslog.JBossLog;

/**
 * Entry point into the ordering system.
 * This class provides the API endpoint for initiating product purchase orders.
 */
@Path("/api/v1/opg")
@JBossLog
@Tag(name = "Purchase Order", description = "Operations related to purchasing orders")
public class PurchaseOrderGatewayResource {

    @ConfigProperty(name = "quarkus.temporal.worker.task-queue")
    String taskQueue;

    @Inject
    WorkflowClient client;

    /**
     * Initiates the product purchase process by starting a workflow that interacts
     * with various services to create a new order and notify the user.
     *
     * @param request The order purchase request containing details about the order.
     * @return A Response object with the transaction ID of the initiated workflow.
     * @throws RuntimeException if there's an error processing the order purchase
     *                          request.
     */
    @Path("/purchase")
    @Timed
    @POST
    @Operation(summary = "Initiate a product purchase", description = "Starts the workflow to process a new order purchase")
    @APIResponse(responseCode = "202", description = "Order purchase request accepted", content = @Content(mediaType = "application/json", schema = @Schema(implementation = WorkflowInitiationResponse.class)))
    @APIResponse(responseCode = "500", description = "Internal server error")
    public Response purchaseOrder(
            @Valid @Schema(implementation = OrderPurchaseRequest.class) OrderPurchaseRequest request) {
        log.infof("Initiating order purchase request with incoming request - %s", request);

        try {
            // Get the transaction id from the request
            UUID requestId = UUID.fromString(MDC.get(RequestIdFilters.REQUEST_ID_MDC_KEY));

            // Start the workflow
            PurchaseOrderWorkflow workflow = client.newWorkflowStub(PurchaseOrderWorkflow.class,
                    WorkflowOptions.newBuilder()
                            .setWorkflowId("OrderPurchase-" + requestId.toString())
                            .setTaskQueue(taskQueue).build());

            // Create the context
            PurchaseOrderContext ctx = PurchaseOrderContext.builder()
                    .transactionId(requestId)
                    .customerEmail(request.getCustomerEmail())
                    .creditCard(request.getCreditCard())
                    .products(request.getProducts())
                    .requestDate(ZonedDateTime.now())
                    .requestedByHost(MDC.get(RequestIdFilters.REQUEST_IP_MDC_KEY))
                    .requestedByUser(MDC.get(RequestIdFilters.REQUEST_USER_MDC_KEY))
                    .build();

            WorkflowClient.start(workflow::placeOrder, ctx);
            return Response.accepted(WorkflowInitiationResponse.builder()
                    .transactionId(requestId)
                    .build())
                    .build();
        } catch (Exception e) {
            log.error("Error processing order purchase request", e);
            throw new RuntimeException("Error processing order purchase request", e);
        }
    }
}