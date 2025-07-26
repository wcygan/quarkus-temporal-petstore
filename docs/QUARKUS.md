# Quarkus Implementation Guide

This document provides a comprehensive overview of how Quarkus is implemented across the Temporal Petstore microservices application.

## Table of Contents
- [Architecture Overview](#architecture-overview)
- [Core Patterns](#core-patterns)
- [Service Implementation](#service-implementation)
- [Configuration Management](#configuration-management)
- [Database Integration](#database-integration)
- [REST API Design](#rest-api-design)
- [Temporal Integration](#temporal-integration)
- [Extensions Used](#extensions-used)
- [Development Features](#development-features)
- [Performance Optimizations](#performance-optimizations)

## Architecture Overview

The application consists of 6 Quarkus microservices orchestrated by Temporal workflows:

```
┌─────────────────────────────────────────────────────────────────┐
│                    Purchase Order Gateway                        │
│                 (Quarkus + PrimeFaces + REST)                   │
│                        Port: 8082                               │
└───────────────────────────┬─────────────────────────────────────┘
                            │
                ┌───────────▼───────────┐
                │   Temporal Server     │
                │  (Workflow Engine)    │
                └───────────┬───────────┘
                            │
        ┌───────────────────┴───────────────────────┐
        │           Activity Workers                 │
        ├───────────────────┬───────────────────────┤
┌───────▼────────┐ ┌────────▼────────┐ ┌──────────▼────────┐
│ Notification   │ │ Order Service   │ │ Payment Service   │
│   Service      │ │   (Postgres)    │ │                   │
│  Port: 8089    │ │  Port: 8090     │ │   Port: 8086      │
└────────────────┘ └─────────────────┘ └───────────────────┘
┌─────────────────┐ ┌─────────────────┐
│ Warehouse       │ │ Shipment        │
│   Service       │ │   Service       │
│  Port: 8989     │ │  Port: 8877     │
└─────────────────┘ └─────────────────┘
```

## Core Patterns

### Dependency Injection

Quarkus leverages Jakarta CDI for compile-time dependency injection:

```java
@ApplicationScoped  // Singleton scope
public class OrderService {
    @Inject
    OrderRepository orderRepo;
    
    @Inject
    Event<OrderCreatedEvent> orderCreatedEvent;
}
```

### Configuration Injection

Using MicroProfile Config for externalized configuration:

```java
@ConfigProperty(name = "app.email.from", defaultValue = "noreply@petstore.com")
String fromEmail;

@ConfigProperty(name = "quarkus.temporal.worker.task-queue")
String taskQueue;
```

### Logging

Consistent logging with JBoss Logging:

```java
@ApplicationScoped
@JBossLog
public class PaymentService {
    public ProcessPaymentResponse processPayment(ProcessPaymentRequest request) {
        log.infof("Processing payment for order %s", request.getOrderNumber());
    }
}
```

## Service Implementation

### Service Layer Pattern

Each microservice follows a consistent three-layer architecture:

```java
// 1. REST Resource Layer
@Path("/api/v1/orders")
public class OrderResource {
    @Inject OrderService service;
    
    @POST
    @Transactional
    public Response createOrder(@Valid CreateOrderRequest request) {
        return Response.ok(service.createOrder(request)).build();
    }
}

// 2. Service Layer
@ApplicationScoped
public class OrderService {
    @Inject OrderRepository repository;
    
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        // Business logic
    }
}

// 3. Repository Layer
@ApplicationScoped
public class OrderRepository extends PanacheRepositoryBase<OrderEntity, UUID> {
    // Data access methods
}
```

### Temporal Activity Implementation

Activities are implemented as CDI beans:

```java
@ApplicationScoped
public class OrderServiceActivitiesImpl implements OrderServiceActivities {
    @Inject
    OrderService orderService;
    
    @Override
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        return orderService.createOrder(request);
    }
}
```

## Configuration Management

### Application Properties

Each service has environment-specific configurations:

```properties
# Service Identity
quarkus.application.name=order-service

# HTTP Configuration
%dev.quarkus.http.port=8090
%prod.quarkus.http.port=8080

# Database Configuration
quarkus.datasource.db-kind=postgresql
quarkus.datasource.jdbc.url=${POSTGRES_URL:jdbc:postgresql://localhost:5432/postgres}
quarkus.datasource.username=${POSTGRES_USER:postgres}
quarkus.datasource.password=${POSTGRES_PASSWORD:changeme}

# Hibernate Configuration
%dev.quarkus.hibernate-orm.database.generation=drop-and-create
%prod.quarkus.hibernate-orm.database.generation=validate

# Temporal Configuration
quarkus.temporal.connection.target=${TEMPORAL_SERVER_URL:localhost:7233}
quarkus.temporal.worker.task-queue=order-fulfillment-queue
```

### Environment Variables

Key environment variables with defaults:

- `TEMPORAL_SERVER_URL`: Temporal server address (default: localhost:7233)
- `POSTGRES_URL`: PostgreSQL connection URL
- `SMTP_HOST`: Mail server host (default: localhost)
- `SMTP_PORT`: Mail server port (default: 2025)

## Database Integration

### Hibernate ORM with Panache

Entities extend PanacheEntityBase for custom ID types:

```java
@Entity
@Table(name = "orders")
public class OrderEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue
    private UUID id;
    
    @Column(name = "order_number", unique = true)
    private String orderNumber;
    
    @Column(name = "transaction_id")
    private String transactionId;
    
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
}
```

### Transaction Management

Using declarative transactions:

```java
@Transactional
public void markOrderAsComplete(MarkOrderCompleteRequest request) {
    OrderEntity order = orderRepo.find("transactionId", request.getTransactionId())
        .withLock(LockModeType.PESSIMISTIC_WRITE)
        .firstResult();
    
    order.setStatus(OrderStatus.COMPLETE);
    order.setShipmentTrackingNumber(request.getShipmentTrackingNumber());
}
```

## REST API Design

### JAX-RS Implementation

RESTful endpoints with OpenAPI documentation:

```java
@Path("/api/v1/opg")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Purchase Order", description = "Purchase order operations")
public class PurchaseOrderGatewayResource {
    
    @POST
    @Path("/purchase")
    @Operation(summary = "Initiate a product purchase")
    @APIResponse(responseCode = "202", description = "Order accepted")
    @APIResponse(responseCode = "400", description = "Invalid request")
    public Response purchaseOrder(@Valid OrderPurchaseRequest request) {
        // Start workflow asynchronously
        return Response.accepted().build();
    }
}
```

### Request/Response Models

Using immutable DTOs with builders:

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    @NotBlank
    private String orderNumber;
    
    @Email
    @NotBlank
    private String customerEmail;
    
    @Valid
    @NotEmpty
    private List<OrderItem> items;
}
```

### Error Handling

RFC 7807 Problem Details for error responses:

```java
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception exception) {
        return Response.status(Status.INTERNAL_SERVER_ERROR)
            .entity(Problem.builder()
                .withTitle("Internal Server Error")
                .withDetail(exception.getMessage())
                .build())
            .type(MediaType.APPLICATION_PROBLEM_JSON)
            .build();
    }
}
```

## Temporal Integration

### Worker Configuration

Workers are configured as CDI beans:

```java
@ApplicationScoped
public class WorkerProducer {
    @Produces
    @ApplicationScoped
    public Worker orderServiceWorker(
        WorkerFactory factory,
        OrderServiceActivitiesImpl activities) {
        
        Worker worker = factory.newWorker("order-fulfillment-queue");
        worker.registerActivitiesImplementations(activities);
        return worker;
    }
}
```

### MDC Context Propagation

Request context flows through Temporal activities:

```java
@ServerRequestFilter
public void addRequestInformation(ContainerRequestContext context) {
    String requestId = UUID.randomUUID().toString();
    MDC.put("requestId", requestId);
    context.getHeaders().add("X-Request-ID", requestId);
}
```

## Extensions Used

### Core Extensions
- `quarkus-rest`: Modern JAX-RS implementation
- `quarkus-rest-jackson`: JSON serialization
- `quarkus-arc`: CDI dependency injection
- `quarkus-config-yaml`: YAML configuration support

### Data Extensions
- `quarkus-hibernate-orm-panache`: Simplified JPA
- `quarkus-jdbc-postgresql`: PostgreSQL driver
- `quarkus-hibernate-validator`: Bean validation

### Integration Extensions
- `quarkus-temporal`: Workflow orchestration
- `quarkus-mailer`: Email support
- `quarkus-qute`: Template engine
- `quarkus-primefaces`: JSF UI framework

### Observability Extensions
- `quarkus-smallrye-openapi`: OpenAPI/Swagger
- `quarkus-smallrye-health`: Health checks
- `quarkus-opentelemetry`: Distributed tracing
- `quarkus-micrometer`: Metrics

## Development Features

### Dev Mode

Run services in development mode with hot reload:

```bash
mvn quarkus:dev
```

Features available in dev mode:
- Live coding with automatic restart
- Dev UI at http://localhost:8090/q/dev-ui/
- Configuration profiles (%dev)
- Enhanced logging
- Dev services (if configured)

### Dev UI Features

Each service provides a Dev UI with:
- Configuration editor
- Extension information
- Health checks
- OpenAPI explorer
- Database schema viewer

### Testing Support

Test dependencies included:

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-junit5</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <scope>test</scope>
</dependency>
```

## Performance Optimizations

### Build Time Optimization

Quarkus performs dependency injection and configuration at build time:

```bash
# Standard JVM build
mvn clean package

# Native image build
mvn clean package -Pnative
```

### Runtime Optimizations

1. **Connection Pooling**:
   ```properties
   quarkus.datasource.jdbc.max-size=20
   quarkus.datasource.jdbc.min-size=5
   ```

2. **HTTP Configuration**:
   ```properties
   quarkus.http.limits.max-connections=100
   quarkus.http.idle-timeout=30s
   ```

3. **Transaction Boundaries**:
   ```java
   @Transactional(Transactional.TxType.REQUIRES_NEW)
   public void processInNewTransaction() {
       // Isolated transaction
   }
   ```

### Monitoring and Metrics

Micrometer integration for metrics:

```java
@Timed(value = "order.creation.time", description = "Order creation duration")
public CreateOrderResponse createOrder(CreateOrderRequest request) {
    // Method implementation
}
```

## Best Practices

### 1. Use Constructor Injection for Required Dependencies
```java
@ApplicationScoped
public class OrderService {
    private final OrderRepository repository;
    
    @Inject
    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }
}
```

### 2. Leverage Configuration Profiles
```properties
# Development
%dev.quarkus.log.level=DEBUG

# Production
%prod.quarkus.log.level=INFO
```

### 3. Implement Health Checks
```java
@Liveness
@ApplicationScoped
public class LivenessCheck implements HealthCheck {
    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.up("Service is alive");
    }
}
```

### 4. Use Panache Query Methods
```java
public Optional<OrderEntity> findByOrderNumber(String orderNumber) {
    return find("orderNumber", orderNumber).firstResultOptional();
}
```

### 5. Handle Async Operations Properly
```java
@Asynchronous
public CompletionStage<Void> processAsync(String data) {
    return CompletableFuture.runAsync(() -> {
        // Async processing
    });
}
```

## Troubleshooting

### Common Issues

1. **Port Conflicts**: Each service runs on a different port in dev mode
2. **Database Connection**: Ensure PostgreSQL is running (via docker-compose)
3. **Temporal Connection**: Verify Temporal server is accessible
4. **Hot Reload Issues**: Clean and rebuild if changes aren't detected

### Debug Configuration

Enable debug logging:

```properties
quarkus.log.category."com.melloware".level=DEBUG
quarkus.log.console.format=%d{HH:mm:ss} %-5p [%c{2.}] (%t) %s%e%n
```

### Native Image Issues

For reflection-based libraries, configure in `reflection-config.json`:

```json
[
  {
    "name": "com.melloware.petstore.order.OrderEntity",
    "allDeclaredFields": true,
    "allDeclaredMethods": true
  }
]
```