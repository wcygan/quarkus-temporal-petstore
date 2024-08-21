package com.mycompany.order.purchasing.gateway.app;

import java.time.ZonedDateTime;
import java.util.UUID;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logmanager.MDC;

import com.mycompany.order.purchasing.gateway.app.filters.RequestIdFilters;
import com.mycompany.order.purchasing.gateway.app.temporal.OrderPurchaseWorkflow;
import com.mycompany.order.purchasing.shared.models.json.OrderPurchaseRequest;
import com.mycompany.order.purchasing.shared.models.json.WorkflowInitiationResponse;

import io.micrometer.core.annotation.Timed;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;

import lombok.extern.jbosslog.JBossLog;

/**
 * Entry into the ordering system.
 */
@Path("/api/v1/opg")
@JBossLog
public class OrderPurchaseGateway {

    @ConfigProperty(name = "quarkus.temporal.worker.task-queue")
    String taskQueue;

    @Inject
    WorkflowClient client;

    /**
     * This method is used to start the product purchase process and will start
     * the workflow to work with various services to create new order and
     * ultimately notify the user
     *
     * @param request {@link OrderPurchaseRequest}
     * @return
     */
    @Path("/purchase")
    @Timed
    @POST
    public Response purchaseOrder(@Valid OrderPurchaseRequest request) {
        log.infof("Initiating order purchase request with incoming request - %s", request);

        try {

            // Get the transaction id from the request
            UUID requestId = UUID.fromString(MDC.get(RequestIdFilters.REQUEST_ID_MDC_KEY));

            // Start the workflow
            OrderPurchaseWorkflow workflow = client.newWorkflowStub(OrderPurchaseWorkflow.class,
                    WorkflowOptions.newBuilder()
                            .setWorkflowId("OrderPurchase-" + requestId.toString())
                            .setTaskQueue(taskQueue).build());

            // Create the context
            OrderPurchaseContext ctx = OrderPurchaseContext.builder()
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
            throw new RuntimeException("Error processing order  purchase request", e);
        }
    }
}
