# Temporal Integration Guide

This document describes how Temporal workflow orchestration is integrated into the Quarkus Petstore application.

## Overview

The application uses [Temporal](https://temporal.io/) to orchestrate a distributed e-commerce order fulfillment process across 6 microservices. The integration leverages the [Quarkiverse Temporal extension](https://github.com/quarkiverse/quarkus-temporal) for seamless Quarkus integration.

## Architecture

### Service Communication Flow

```
┌─────────────────────────┐
│  Purchase Order Gateway │ ──────┐
│    (Workflow Host)      │       │
└─────────────────────────┘       │
                                  ▼
                        ┌──────────────────┐
                        │ Temporal Server  │
                        │  (localhost:7233)│
                        └──────────────────┘
                                  │
         ┌────────────────────────┼────────────────────────┐
         │                        │                        │
         ▼                        ▼                        ▼
┌──────────────────┐    ┌──────────────────┐    ┌──────────────────┐
│ Notification     │    │ Order Service    │    │ Payment Service  │
│ Service          │    │                  │    │                  │
└──────────────────┘    └──────────────────┘    └──────────────────┘
```

### Key Concepts

- **Workflow**: The main orchestration logic (`PurchaseOrderWorkflow`)
- **Activities**: Service operations exposed as Temporal activities
- **Task Queues**: Service-specific queues for activity execution
- **Saga Pattern**: Compensation-based rollback mechanism

## Workflow Implementation

### Workflow Definition

The main workflow is defined in `purchase-order-gateway`:

```java
@WorkflowInterface
public interface PurchaseOrderWorkflow {
    @WorkflowMethod(name = "placeOrder")
    void placeOrder(@Valid @NotNull PurchaseOrderContext ctx);
}
```

### Order Processing Steps

1. Send order acknowledgment email
2. Create order record in database
3. Calculate order total
4. Process payment (with compensation setup)
5. Check warehouse inventory
6. Generate shipment tracking number
7. Complete order and send success notification

### Workflow Execution

```java
// Create workflow instance
PurchaseOrderWorkflow workflow = client.newWorkflowStub(
    PurchaseOrderWorkflow.class,
    WorkflowOptions.newBuilder()
        .setWorkflowId("OrderPurchase-" + requestId.toString())
        .setTaskQueue("purchase-order-tasks")
        .build()
);

// Execute asynchronously
WorkflowClient.start(workflow::placeOrder, ctx);
```

## Activity Pattern

### Activity Interface (temporal-common)

```java
@ActivityInterface
public interface PaymentActivities {
    DebitCreditCardResponse debitCreditCard(@Valid @NotNull DebitCreditCardRequest request);
    void reversePaymentTransactions(@Valid @NotNull ReverseActionsForTransactionRequest request);
}
```

### Activity Implementation (service)

```java
@ApplicationScoped
public class PaymentActivitiesImpl implements PaymentActivities {
    @Inject
    PaymentService service;
    
    @Override
    public DebitCreditCardResponse debitCreditCard(DebitCreditCardRequest request) {
        return service.debitAccount(request);
    }
}
```

### Activity Registration

The Quarkus extension automatically:
- Discovers `@ApplicationScoped` classes implementing `@ActivityInterface`
- Registers them with the configured task queue
- Manages worker lifecycle

## Saga Pattern for Compensations

### Compensation Setup

```java
// Initialize saga
Saga saga = new Saga(new Saga.Options.Builder().build());

// Register compensation before risky operation
saga.addCompensation(() -> 
    paymentActivity.reversePaymentTransactions(reverseRequest)
);

// Execute risky operation
paymentActivity.debitCreditCard(cardRequest);

// On failure, compensations run automatically
```

### Cleanup Pattern

```java
// Cleanup runs in detached cancellation scope
Workflow.newDetachedCancellationScope(() -> 
    cleanup(e, saga, orderCtx, transactionId)
).run();
```

## Configuration

### Service Configuration

Each service configures Temporal in `application.properties`:

```properties
# Temporal connection
quarkus.temporal.connection.target=${TEMPORAL_SERVER_URL:localhost:7233}
quarkus.temporal.namespace=default

# Worker configuration
quarkus.temporal.worker.task-queue=payment-tasks
quarkus.temporal.workflow.workflow-task-timeout=5s

# Enable MDC propagation
quarkus.temporal.context-propagators=com.melloware.petstore.common.context.MDCContextPropagator
```

### Activity Options

```java
ActivityOptions options = ActivityOptions.newBuilder()
    .setStartToCloseTimeout(Duration.ofSeconds(30))
    .setRetryOptions(RetryOptions.newBuilder()
        .setDoNotRetry(
            BadPaymentInfoException.class.getName(),
            OutOfStockException.class.getName()
        )
        .setInitialInterval(Duration.ofSeconds(1))
        .setMaximumInterval(Duration.ofSeconds(100))
        .setBackoffCoefficient(2)
        .setMaximumAttempts(500)
        .build())
    .build();
```

## Task Queue Architecture

| Service | Task Queue | Purpose |
|---------|------------|---------|
| Purchase Order Gateway | purchase-order-tasks | Workflow execution |
| Payment Service | payment-tasks | Payment processing |
| Order Service | order-tasks | Order persistence |
| Notification Service | notification-tasks | Email notifications |
| Warehouse Service | warehouse-tasks | Inventory management |
| Shipment Service | shipment-tasks | Tracking generation |

## Context Propagation

### MDC Propagator

The application propagates request context across service boundaries:

```java
@Singleton
@Unremovable
public class MDCContextPropagator implements ContextPropagator {
    @Override
    public Object getCurrentContext() {
        Map<String, String> context = new HashMap<>();
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();
        if (mdcContext != null) {
            mdcContext.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("X-"))
                .forEach(entry -> context.put(entry.getKey(), entry.getValue()));
        }
        return context;
    }
}
```

### Usage

Request IDs and other context automatically flow through the workflow:
- `X-Request-ID`: Unique request identifier
- `X-Transaction-ID`: Business transaction identifier

## Error Handling

### Business Rule Failures

| Condition | Exception | Effect |
|-----------|-----------|--------|
| Order total > $1000 | PaymentDeclinedException | Payment fails, order cancelled |
| Email = "bad_customer@foo.com" | BadPaymentInfoException | Immediate rejection |
| Total quantity > 20 | OutOfStockException | Inventory check fails |

### Exception Handling Pattern

```java
public static boolean isExceptionType(Exception e, Class<?> exceptionClass) {
    if (e.getCause() instanceof ApplicationFailure) {
        String exceptionType = ((ApplicationFailure) (e.getCause())).getType();
        return exceptionType.equals(exceptionClass.getName());
    }
    return false;
}
```

## Development Tips

### Running Temporal Locally

```bash
# Start Temporal server
cd docker-compose && docker-compose up -d temporal

# Access Temporal Web UI
open http://localhost:8080
```

### Debugging Workflows

1. Use Temporal Web UI to view workflow history
2. Enable debug logging:
   ```properties
   quarkus.log.category."io.temporal".level=DEBUG
   ```
3. Use workflow replays for debugging

### Testing Activities

```java
@QuarkusTest
class PaymentActivitiesTest {
    @Inject
    PaymentActivities activities;
    
    @Test
    void testDebitCard() {
        DebitCreditCardRequest request = new DebitCreditCardRequest();
        // ... setup request
        
        DebitCreditCardResponse response = activities.debitCreditCard(request);
        assertNotNull(response.getChargeId());
    }
}
```

## Best Practices

### 1. Activity Design
- Keep activities focused and single-purpose
- Use meaningful task queue names
- Set appropriate timeouts

### 2. Error Handling
- Define non-retryable exceptions for business failures
- Use saga pattern for compensations
- Log errors with context

### 3. Performance
- Use appropriate retry policies
- Configure worker count based on load
- Monitor workflow execution times

### 4. Observability
- Propagate request IDs via MDC
- Use structured logging
- Monitor Temporal metrics

## Monitoring

### Key Metrics

- Workflow execution time
- Activity failure rates
- Task queue depth
- Worker utilization

### Temporal UI Features

- Workflow search and filtering
- Execution history visualization
- Activity timeline
- Error details and stack traces

## Troubleshooting

### Common Issues

1. **Worker not picking up tasks**
   - Check task queue name matches
   - Verify worker is running
   - Check Temporal connection

2. **Activity timeouts**
   - Increase `StartToCloseTimeout`
   - Check for blocking operations
   - Verify service is healthy

3. **Workflow stuck**
   - Check Temporal UI for pending activities
   - Look for unhandled exceptions
   - Verify all compensations complete

### Debug Commands

```bash
# View workflow execution
temporal workflow show --workflow-id OrderPurchase-{UUID}

# List workers
temporal task-queue describe --task-queue payment-tasks

# Check workflow history
temporal workflow list --query 'WorkflowType="placeOrder"'
```

## References

- [Temporal Documentation](https://docs.temporal.io/)
- [Quarkus Temporal Extension](https://github.com/quarkiverse/quarkus-temporal)
- [Saga Pattern in Temporal](https://docs.temporal.io/application-development/features#saga)
- [MDC Propagation](https://docs.temporal.io/concepts/workflow-message-passing#context-propagation)