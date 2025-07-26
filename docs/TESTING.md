# Testing Guide

This guide covers how to run tests for the Quarkus Temporal Petstore application.

## Prerequisites

Before running tests, ensure the required infrastructure is running:

```bash
cd docker-compose && docker-compose up -d
```

This starts:
- Temporal Server
- PostgreSQL Database
- Mailpit (SMTP Server)
- Jaeger (Tracing)

## Running Tests

### Run All Tests (Unit + Integration)

```bash
mvn clean test verify
```

This command:
- Cleans previous build artifacts
- Runs all unit tests
- Runs integration tests
- Generates test reports

### Run Only Unit Tests

```bash
mvn clean test
```

### Run Tests Without Cleaning

```bash
mvn test
```

## Module-Specific Testing

### Test a Single Module

```bash
cd <module-directory> && mvn test

# Examples:
cd temporal-common && mvn test
cd order-service && mvn test
cd payment-service && mvn test
cd warehouse-service && mvn test
cd shipment-service && mvn test
cd notification-service && mvn test
cd purchase-order-gateway && mvn test
```

### Test Multiple Specific Modules

```bash
mvn test -pl temporal-common,order-service
```

## Running Specific Tests

### Run a Specific Test Class

```bash
mvn test -Dtest=TestClassName

# Examples:
mvn test -Dtest=OrderServiceTest
mvn test -Dtest=PaymentActivityTest
```

### Run a Specific Test Method

```bash
mvn test -Dtest=TestClassName#methodName

# Examples:
mvn test -Dtest=OrderServiceTest#testCreateOrder
mvn test -Dtest=PaymentActivityTest#testPaymentFailure
```

### Run Tests Matching a Pattern

```bash
mvn test -Dtest="*Service*"
mvn test -Dtest="Payment*Test"
```

## Development Mode Testing

Quarkus provides an interactive development mode with hot reload and test execution:

### Start Dev Mode

```bash
cd <module-directory> && mvn quarkus:dev
```

### Dev Mode Commands

While in dev mode:
- Press `r` - Re-run all tests
- Press `f` - Re-run failed tests only
- Press `o` - Toggle test output
- Press `i` - Toggle instrumentation based reload
- Press `s` - Force restart
- Press `h` - Show help
- Press `q` - Quit

## Skip Tests

### Skip Tests During Build

```bash
mvn clean install -DskipTests
```

### Skip Only Integration Tests

```bash
mvn clean install -DskipITs
```

## Test Profiles

### Run with Native Profile

```bash
mvn test -Pnative
```

### Run with Specific Test Profile

```bash
mvn test -Dquarkus.test.profile=test
```

## Test Reports

Test reports are generated in:
- `target/surefire-reports/` - Unit test reports
- `target/failsafe-reports/` - Integration test reports

### Generate HTML Test Report

```bash
mvn surefire-report:report
```

View the report at: `target/site/surefire-report.html`

## Debugging Tests

### Run Tests in Debug Mode

```bash
mvn test -Dmaven.surefire.debug
```

This will pause and wait for a debugger to connect on port 5005.

### Run with Specific JVM Arguments

```bash
mvn test -DargLine="-Xmx1024m -XX:+PrintGCDetails"
```

## Continuous Testing

For continuous testing during development:

```bash
mvn quarkus:test
```

This watches for changes and automatically re-runs affected tests.

## Test Coverage

### Generate Coverage Report with JaCoCo

First, add JaCoCo plugin to your pom.xml if not already present, then:

```bash
mvn clean test jacoco:report
```

Coverage report will be at: `target/site/jacoco/index.html`

## Common Test Scenarios

### Testing Order Workflow Success

```bash
cd purchase-order-gateway
mvn test -Dtest=PurchaseOrderWorkflowTest#testSuccessfulOrder
```

### Testing Payment Failures

```bash
cd payment-service
mvn test -Dtest=PaymentActivityTest#testPaymentOverLimit
```

### Testing Compensation Logic

```bash
cd purchase-order-gateway
mvn test -Dtest=PurchaseOrderWorkflowTest#testOrderWithPaymentFailure
```

## Troubleshooting

### Tests Failing Due to Port Conflicts

Ensure no services are running on required ports:
- 8082 (Gateway)
- 8089 (Notification)
- 8090 (Order)
- 8086 (Payment)
- 8989 (Warehouse)
- 8877 (Shipment)

### Database Connection Issues

Verify PostgreSQL is running:
```bash
docker ps | grep postgres
```

### Temporal Connection Issues

Check Temporal is accessible:
```bash
curl http://localhost:7233/health
```

### View Test Logs

```bash
# View specific test output
cat target/surefire-reports/com.example.TestClass-output.txt

# View all test logs
find target/surefire-reports -name "*.txt" -exec cat {} \;
```

## Best Practices

1. **Always run tests before committing**
   ```bash
   mvn clean test verify
   ```

2. **Use Dev Mode for rapid feedback**
   ```bash
   mvn quarkus:dev
   ```

3. **Run integration tests regularly**
   ```bash
   mvn verify
   ```

4. **Keep tests isolated**
   - Each test should be independent
   - Use `@TestTransaction` for database tests
   - Mock external dependencies

5. **Write meaningful test names**
   - Use descriptive method names
   - Follow pattern: `test<What>_<When>_<Expected>`

## CI/CD Integration

For CI/CD pipelines, use:

```bash
mvn clean test verify -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn
```

This:
- Runs in batch mode (-B)
- Reduces download progress output
- Suitable for automated environments