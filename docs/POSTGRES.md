# PostgreSQL Database Integration

## Overview

This application uses PostgreSQL as its primary database, with a minimalist persistence architecture where only the **order-service** requires database access. All other microservices operate statelessly, relying on Temporal workflows for orchestration.

## Architecture

```
PostgreSQL (5432)
    │
    └── order-service (8090)
            ├── OrderEntity
            └── OrderLineItemEntity
```

## Database Configuration

### Connection Details

```properties
# Default connection parameters
Host: localhost
Port: 5432
Database: postgres
Username: temporal
Password: temporal
```

### Order Service Configuration

Located in `order-service/src/main/resources/application.properties`:

```properties
# Database connection
quarkus.datasource.db-kind=postgresql
quarkus.datasource.username=${POSTGRES_USER:temporal}
quarkus.datasource.password=${POSTGRES_PASSWORD:temporal}
quarkus.datasource.jdbc.url=${POSTGRES_URL:jdbc:postgresql://localhost:5432/postgres}

# Connection pool settings
quarkus.datasource.jdbc.max-size=20
quarkus.datasource.jdbc.min-size=5
quarkus.datasource.jdbc.telemetry=true

# Schema management
%dev.quarkus.hibernate-orm.database.generation=drop-and-create
%prod.quarkus.hibernate-orm.database.generation=validate
```

## Database Schema

### Orders Table

```sql
CREATE TABLE orders (
    id UUID PRIMARY KEY,
    order_number VARCHAR NOT NULL UNIQUE,
    transaction_id UUID NOT NULL UNIQUE,
    customer_email VARCHAR NOT NULL,
    order_date TIMESTAMP NOT NULL,
    status VARCHAR NOT NULL,
    order_total DECIMAL NOT NULL,
    tracking_number VARCHAR
);
```

### Order Line Items Table

```sql
CREATE TABLE order_line_items (
    id UUID PRIMARY KEY,
    order_id UUID REFERENCES orders(id),
    product_sku VARCHAR NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL NOT NULL
);
```

## Data Access Layer

### Entity Model

The application uses JPA entities with Hibernate ORM:

- **OrderEntity**: Main order information including customer details, status, and totals
- **OrderLineItemEntity**: Individual items within an order

### Repository Pattern

Data access follows a layered repository approach:

1. **CustomIDBaseRepository**: Generic CRUD operations with metrics
2. **OrderRepository**: Order-specific queries using Panache

Example queries:
```java
// Find by order number
find("orderNumber = ?1", orderNumber).firstResultOptional();

// Find by transaction ID
find("transactionId = ?1", transactionId).firstResultOptional();
```

### Transaction Management

All database operations use declarative transactions:
```java
@Transactional
public CreateOrderResponse createOrder(CreateOrderRequest request) {
    // Automatic transaction management
}
```

## Connection Pooling

The application uses **Agroal** (Quarkus default) with:
- Minimum connections: 5
- Maximum connections: 20
- Connection validation enabled
- Telemetry for monitoring

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| POSTGRES_USER | Database username | temporal |
| POSTGRES_PASSWORD | Database password | temporal |
| POSTGRES_URL | JDBC connection URL | jdbc:postgresql://localhost:5432/postgres |

## Development vs Production

### Development Mode
- Schema: Automatically dropped and recreated on startup
- Data: No seed data or migrations
- Connection: Local PostgreSQL instance

### Production Mode
- Schema: Validation only (no automatic changes)
- Data: Manual schema management required
- Connection: Configure via environment variables

## Monitoring

### Enabled Features
- JDBC telemetry via OpenTelemetry
- Connection pool metrics
- Micrometer timing on repository operations

### Key Metrics
- Connection pool usage
- Query execution times
- Transaction durations

## Best Practices

### Current Implementation
1. ✅ Environment-based configuration
2. ✅ Connection pooling with reasonable limits
3. ✅ Proper entity relationships
4. ✅ Repository abstraction
5. ✅ Declarative transaction management

### Recommendations

1. **Add Database Indexes**
   ```sql
   CREATE INDEX idx_orders_status ON orders(status);
   CREATE INDEX idx_orders_customer_email ON orders(customer_email);
   ```

2. **Enable SSL Connections**
   ```properties
   quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/postgres?sslmode=require
   ```

3. **Implement Schema Migrations**
   - Add Flyway or Liquibase for version control
   - Track schema changes in version control

4. **Configure Production Pool Sizing**
   - Base on load testing results
   - Monitor connection wait times

## Troubleshooting

### Common Issues

1. **Connection Refused**
   - Verify PostgreSQL is running: `docker ps | grep postgresql`
   - Check port availability: `lsof -i :5432`

2. **Authentication Failed**
   - Verify credentials in environment variables
   - Check PostgreSQL user permissions

3. **Schema Mismatch**
   - In production, manually apply schema changes
   - In development, restart with `drop-and-create`

### Useful Commands

```bash
# Connect to database
docker exec -it temporal-postgresql psql -U temporal -d postgres

# View tables
\dt

# View order data
SELECT * FROM orders;

# Check active connections
SELECT * FROM pg_stat_activity WHERE datname = 'postgres';
```

## Performance Considerations

### Current State
- No caching layer implemented
- Direct database queries for all operations
- Single database for all order data

### Optimization Opportunities
1. Implement query result caching
2. Add read replicas for scalability
3. Consider partitioning for large datasets
4. Enable prepared statement caching