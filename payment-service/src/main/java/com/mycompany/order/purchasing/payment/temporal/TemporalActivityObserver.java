package com.mycompany.order.purchasing.payment.temporal;


import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.List;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import com.mycompany.order.purchasing.shared.utils.MDCContextPropagator;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

/**
 * This class attempts to connect to the Temporal server on Quarkus startup and will
 * also close any connections on shutdown
 * 
 
 */
@ApplicationScoped
public class TemporalActivityObserver {

    private WorkflowClient client;
    private WorkerFactory factory;

    @Inject
    Logger log;

    @ConfigProperty(name = "temporal.order.purchase.activity.task.queue")
    String taskQueue;

    @ConfigProperty(name = "temporal.order.purchase.workflow.namespace")
    String namespace;

    @ConfigProperty(name = "temporal.server.url")
    String serverLocation;

    @Inject
    PaymentActivitiesImpl activity;
    
    MDCContextPropagator mdcPropagator = new MDCContextPropagator();

    /**
     * Listen for startup and attempt to connect to Temporal server
     * 
     * @param ev 
     */
    void onStart(@Observes StartupEvent ev) {

        // Create options for the WorkflowServiceStubs with the target server location
        WorkflowServiceStubsOptions options = WorkflowServiceStubsOptions.newBuilder()
                .setTarget(serverLocation) // Specify the server location
                .build();

        // Create a new WorkflowServiceStubs instance with the specified options and a connection timeout of 5 seconds
        WorkflowServiceStubs service = WorkflowServiceStubs.newConnectedServiceStubs(options, Duration.ofSeconds(5));

        // Create WorkflowClientOptions to specify the namespace
        WorkflowClientOptions clientOptions = WorkflowClientOptions.newBuilder()
                .setIdentity("PaymentService-"+ManagementFactory.getRuntimeMXBean().getName())
                .setNamespace(namespace)
                .setContextPropagators(List.of(mdcPropagator))
                .build();

        // Create a new WorkflowClient instance using the connected service stubs and client options
        client = WorkflowClient.newInstance(service, clientOptions);

        // Create a new WorkerFactory instance using the WorkflowClient
        factory = WorkerFactory.newInstance(client);

        // Create a new Worker that listens on the specified task queue
        Worker worker = factory.newWorker(taskQueue);

        // Register activity implementations with the worker
        worker.registerActivitiesImplementations(activity);

        factory.start();
        log.infof("Payment Activity Worker started for task queue: %s", taskQueue);
    }
    
    /**
     * Clean up
     * 
     * @param ev 
     */
    void onStop(@Observes ShutdownEvent ev) {
        factory.shutdown();
    }

}
