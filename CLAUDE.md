# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Quarkus-based microservices petstore application that demonstrates Temporal workflow orchestration for order fulfillment with compensating transactions. The project consists of 6 microservices orchestrated by Temporal workflows to handle e-commerce order processing.

## Essential Commands

### Infrastructure Setup
```bash
# Start all required infrastructure (Temporal, PostgreSQL, Mailpit, Jaeger)
cd docker-compose && docker-compose up -d

# Stop infrastructure
cd docker-compose && docker-compose down
```

### Development Mode
```bash
# Run each service in dev mode (separate terminals)
cd purchase-order-gateway && mvn quarkus:dev    # Port 8082
cd notification-service && mvn quarkus:dev       # Port 8089
cd order-service && mvn quarkus:dev              # Port 8090
cd payment-service && mvn quarkus:dev            # Port 8086
cd warehouse-service && mvn quarkus:dev          # Port 8989
cd shipment-service && mvn quarkus:dev           # Port 8877
```

### Build Commands
```bash
# Build all modules
mvn clean install

# Build without tests
mvn clean install -DskipTests

# Build native images
mvn clean install -Pnative

# Build Docker images
mvn clean install -Pdocker
```

### Testing
```bash
# Run all tests
mvn clean test verify

# Run specific test
mvn test -Dtest=TestClassName#testMethodName

# Run tests for specific module
cd <service-directory> && mvn test
```

## Architecture Overview

### Workflow Orchestration
The system uses Temporal workflows to orchestrate the order fulfillment process:

1. **PurchaseOrderWorkflow** (in purchase-order-gateway) is the main orchestrator
2. Communication happens through Temporal Activities, not direct REST calls
3. Each service implements activity interfaces defined in temporal-common
4. The workflow includes saga-based compensation for rollback on failures

### Order Processing Flow
1. Order submission via Gateway UI or REST API
2. Send order received notification
3. Create order record in database
4. Process payment (with compensation setup)
5. Check warehouse inventory
6. Register shipment and get tracking number
7. Complete order and send success notification
8. On any failure: execute compensations, mark order failed, send error notification

### Service Communication Pattern
- Gateway → Workflow: Direct workflow invocation via WorkflowClient
- Workflow → Services: Through Temporal Activities only
- Each service has its own ActivityImpl that delegates to service logic
- MDC context propagation for request tracking across services

### Compensation Strategy
The workflow uses Temporal's Saga pattern:
- Compensations are registered before risky operations
- Payment reversal is registered before charging
- All compensations run automatically on failure
- Cleanup runs in detached cancellation scope

### Key Failure Scenarios
- Payment failure: Order total > $1000 or email = "bad_customer@foo.com"
- Inventory failure: Total quantity > 20 (across all orders)
- These trigger automatic compensation and rollback

## Module Structure

- **temporal-common**: Shared interfaces, models, and utilities
- **purchase-order-gateway**: Workflow implementation and UI (Port 8082)
- **notification-service**: Email notifications via Qute templates (Port 8089)
- **order-service**: Order persistence with PostgreSQL (Port 8090)
- **payment-service**: Payment validation and processing (Port 8086)
- **warehouse-service**: Inventory checking (Port 8989)
- **shipment-service**: Tracking number generation (Port 8877)

## Access Points

- Main UI: http://localhost:8082/
- Temporal UI: http://localhost:8080/
- Mailpit (Email): http://localhost:8025/
- Jaeger (Tracing): http://localhost:15000/
- pgAdmin: http://localhost:16543/
- Each service Dev UI: http://localhost:<port>/q/dev-ui/

## Important Configuration

Default environment variables:
- TEMPORAL_SERVER_URL: localhost:7233
- POSTGRES_URL: jdbc:postgresql://localhost:5432/postgres
- SMTP_HOST: localhost, SMTP_PORT: 2025
- Workflow timeout: 24 hours

Each service has its own application.properties with service-specific configuration.