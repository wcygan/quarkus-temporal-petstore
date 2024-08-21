package com.mycompany.order.purchasing.gateway.app;

import java.time.Duration;
import java.util.List;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.mycompany.order.purchasing.gateway.app.workflows.OrderPurchaseWorkflowImpl;
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
import lombok.extern.jbosslog.JBossLog;

/**
 * This class attempts to connect to the Temporal server on Quarkus startup and will
 * also close any connections on shutdown
 * 
 
 */
@ApplicationScoped
@JBossLog
public class WorkflowApplicationObserver {

    private WorkflowClient client;
    private WorkerFactory factory;


    @ConfigProperty(name = "temporal.order.purchase.workflow.task.queue")
    String taskQueue;

    @ConfigProperty(name = "temporal.order.purchase.workflow.namespace")
    String namespace;

    @ConfigProperty(name = "temporal.server.url")
    String serverLocation;

    MDCContextPropagator mdcPropagator = new MDCContextPropagator();

    /**
     * Used to configure the clients and stubs on Quarkus startup
     *
     * @param ev
     */
    void onStart(@Observes StartupEvent ev) {
        connectToTemporal();
    }
    
    

    private void connectToTemporal() {

        try {
            
            log.infof("Attempting to connect to Temporal server at %s using taskqueue %s with namespace %s", serverLocation, taskQueue, namespace);
            
            // Create options for the WorkflowServiceStubs with the target server location
            WorkflowServiceStubsOptions options = WorkflowServiceStubsOptions.newBuilder()
                    .setTarget(serverLocation) // Specify the server location
                    .build();

            // Create a new WorkflowServiceStubs instance with the specified options and a connection timeout of 5 seconds
            WorkflowServiceStubs service = WorkflowServiceStubs.newConnectedServiceStubs(options, Duration.ofSeconds(5));

            // Create WorkflowClientOptions to specify the namespace
            WorkflowClientOptions clientOptions = WorkflowClientOptions.newBuilder()
                    .setNamespace(namespace)
                    .setContextPropagators(List.of(mdcPropagator))
                    .build();

            // Create a new WorkflowClient instance using the connected service stubs and client options
            client = WorkflowClient.newInstance(service, clientOptions);

            // Create a new WorkerFactory instance using the WorkflowClient
            factory = WorkerFactory.newInstance(client);

            // Create a new Worker that listens on the specified task queue
            Worker worker = factory.newWorker(taskQueue);

            // Register the workflow implementation class with the worker
            worker.registerWorkflowImplementationTypes(OrderPurchaseWorkflowImpl.class);

            // Start the WorkerFactory to begin polling for workflows and activities
            factory.start();

        } catch (Exception e) {
            log.errorf(e, "Could not connect to Temporal server at %s...will attempt later", serverLocation);
        }
    }

    /**
     * Clean up
     * 
     * @param ev 
     */
    void onStop(@Observes ShutdownEvent ev) {
        factory.shutdown();
    }

    public WorkflowClient getClient() {
        if (client == null) {
            connectToTemporal();
        }

        return client;
    }
}
