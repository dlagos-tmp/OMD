# Order Management Service - Copilot Instructions

## Project Overview
This is a Java Spring Boot microservice for managing orders in an eCommerce platform. The service handles order lifecycle management with PostgreSQL as the primary database.

## Architecture & Design Principles

### Microservice Structure
- **Package Organization**: Use `org.example` as base package with clear separation:
  - `model/` - JPA entities
  - `repository/` - Data access layer
  - `service/` - Business logic layer
  - `controller/` - REST API endpoints
  - `scheduler/` - Scheduled tasks
  - `config/` - Configuration classes

### Code Standards
- Follow Spring Boot best practices and conventions
- Use proper dependency injection with `@Autowired` or constructor injection
- Implement proper exception handling with custom exceptions
- Use validation annotations (`@Valid`, `@NotNull`, `@NotBlank`)
- Follow RESTful API design principles
- Use meaningful variable and method names

## Data Models

### Order Entity
```java
- orderId (Long, Primary Key, Auto-generated)
- customerName (String, Required)
- status (String, Default: "unprocessed")
- orderDate (LocalDateTime, Default: current timestamp)
- orderLines (One-to-Many relationship with OrderLine)
```

### OrderLine Entity
```java
- id (Long, Primary Key, Auto-generated)
- productId (Long, Required)
- quantity (Integer, Required, Positive)
- price (BigDecimal, Required, Positive)
- order (Many-to-One relationship with Order)
```

## Database Configuration
- Use PostgreSQL as the primary database
- Configure connection via `application.properties`
- Use JPA/Hibernate for ORM
- Enable SQL logging for development
- Use `ddl-auto: update` for schema management

## REST API Endpoints

### Order Management (`/orders`)
- `POST /orders` - Create new order with order lines (returns order ID)
- `GET /orders/{id}` - Retrieve order by ID with order lines
- `PUT /orders/{id}` - Update existing order
- `DELETE /orders/{id}` - Delete order and associated order lines

### Response Formats
- Return appropriate HTTP status codes (200, 201, 404, 400, 500)
- Use ResponseEntity for proper HTTP responses
- Handle validation errors gracefully
- Return order ID on creation

## Scheduled Tasks
- Implement a scheduler that runs every 60 seconds
- Update orders with status "unprocessed" to "processed"
- Use `@Scheduled(fixedRate = 60000)` annotation
- Log scheduler execution and results
- Use `@EnableScheduling` on main application class

## Technology Stack
- **Framework**: Spring Boot 2.7.x
- **Database**: PostgreSQL with Spring Data JPA
- **Build Tool**: Maven
- **Java Version**: Java 8
- **JSON Processing**: Jackson (included in Spring Boot)
- **Validation**: Spring Boot Validation Starter

## Development Guidelines

### Entity Relationships
- Use `@JsonManagedReference` and `@JsonBackReference` to handle JSON serialization
- Implement bidirectional relationships properly
- Use `CascadeType.ALL` for Order -> OrderLine relationship
- Use `FetchType.LAZY` for performance optimization

### Service Layer
- Mark service methods with `@Transactional` where appropriate
- Implement proper error handling and validation
- Use Optional<> for methods that might return null
- Keep business logic in service layer, not in controllers

### Repository Layer
- Extend JpaRepository for basic CRUD operations
- Add custom queries using `@Query` annotation when needed
- Use proper naming conventions for query methods

### Configuration
- Use `application.properties` for configuration
- Enable SQL logging in development
- Configure proper database connection settings
- Enable scheduling with `@EnableScheduling`

## Testing Considerations
- Write unit tests for service layer
- Use MockMvc for controller testing
- Test database operations with `@DataJpaTest`
- Test scheduled tasks functionality

## Performance & Best Practices
- Use appropriate fetch strategies (LAZY vs EAGER)
- Implement proper exception handling
- Use validation annotations for input validation
- Follow single responsibility principle
- Use proper HTTP status codes
- Implement proper logging with SLF4J

## Sample Request/Response

### Create Order Request
```json
{
  "customerName": "John Doe",
  "orderLines": [
    {
      "productId": 1,
      "quantity": 2,
      "price": 29.99
    }
  ]
}
```

### Create Order Response
```json
1
```

## Error Handling
- Return appropriate HTTP status codes
- Provide meaningful error messages
- Handle validation errors with proper responses
- Use try-catch blocks in controllers for exception handling

## Logging
- Use SLF4J logger in all classes
- Log important operations (order creation, updates, scheduler runs)
- Log errors and exceptions with proper context
- Use appropriate log levels (INFO, DEBUG, ERROR)

## SQL Script Instructions

### Database Setup
Create a PostgreSQL database and tables with the following SQL script. This script also includes keys and indexes to enhance performance:

```sql
-- Create database
CREATE DATABASE order_management;

-- Switch to the database
\c order_management;

-- Create tables
CREATE TABLE orders (
    order_id SERIAL PRIMARY KEY,
    customer_name VARCHAR(255) NOT NULL,
    status VARCHAR(50) DEFAULT 'unprocessed',
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_lines (
    id SERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    price DECIMAL(10, 2) NOT NULL CHECK (price > 0),
    order_id BIGINT NOT NULL,
    CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES orders (order_id) ON DELETE CASCADE
);

-- Create indexes
CREATE INDEX idx_order_status ON orders (status);
CREATE INDEX idx_order_date ON orders (order_date);
CREATE INDEX idx_order_lines_product_id ON order_lines (product_id);
```

This script ensures the database is optimized for performance and adheres to the schema requirements.

## SQL Script Execution in Docker

To ensure the PostgreSQL database is initialized with the required schema when the Docker container starts, follow these steps:

1. **Prepare the SQL Script**:
   - Save the SQL script provided above into a file named `init.sql`.
   - Place this file in a directory accessible to the Docker container, such as `docker-entrypoint-initdb.d/`.

2. **Update `docker-compose.yml`**:
   - Mount the directory containing `init.sql` to the PostgreSQL container.
   - Ensure the container executes the script during initialization.

3. **Example `docker-compose.yml` Configuration**:
   ```yaml
   version: '3.8'
   services:
     db:
       image: postgres:latest
       container_name: postgres_db
       environment:
         POSTGRES_USER: admin
         POSTGRES_PASSWORD: admin
         POSTGRES_DB: order_management
       volumes:
         - ./docker-entrypoint-initdb.d:/docker-entrypoint-initdb.d
       ports:
         - "5432:5432"
   ```

4. **Verify Initialization**:
   - Start the Docker container using `docker-compose up`.
   - Confirm the database and tables are created by connecting to the PostgreSQL instance and inspecting the schema.

## API Documentation with Swagger

### Swagger/OpenAPI Configuration
- Use SpringDoc OpenAPI for API documentation
- Configure OpenAPI to document all REST endpoints
- Include model schemas in the documentation
- Add detailed descriptions for each endpoint, parameters, and responses
- Configure a proper API title, description, and version

### Swagger UI Setup
- Enable Swagger UI for interactive API testing
- Access Swagger UI at `/swagger-ui.html`
- Configure security for Swagger UI if needed
- Customize the Swagger UI with proper branding and descriptions

### Swagger/OpenAPI Implementation Guidelines
- Annotate model classes with `@Schema` for property descriptions
- Group API endpoints using `@Tag` annotations
- Configure OpenAPI beans properly in a `SwaggerConfig` class

### Example OpenAPI Configuration
```java
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Order Management API")
                        .version("1.0.0")
                        .description("REST API for managing orders in the eCommerce platform"));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/orders/**")
                .build();
    }
}
```

### Maven Dependencies
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-ui</artifactId>
    <version>1.6.12</version>
</dependency>
```

### Testing OpenAPI Integration
- Verify Swagger UI is accessible at `/swagger-ui.html`
- Check that all API endpoints are properly documented
- Ensure model schemas are correctly displayed
- Test API interactions directly from Swagger UI

## Order Processing Log Service

This is a separate microservice designed to log processed order data for auditing and analytics purposes. It is independent of the Order Management Service and should be treated as a distinct microservice with its own project structure, build configuration, and deployment pipeline.

### Purpose & Overview
This service logs processed order data to MongoDB, providing an audit trail of successfully processed orders. It integrates with the main Order Management Service via REST API.

### Architecture
- **Independent Microservice**: This service has its own `pom.xml`, separate from the Order Management Service.
- **REST Endpoint**: A dedicated endpoint for receiving log data from the Order Management Service.
- **MongoDB Storage**: Stores order processing logs in a MongoDB database.
- **Asynchronous Processing**: Ensures minimal impact on the main order flow.

### Project Structure
- **Base Package**: Use `org.example.logservice` as the base package.
- **Modules**:
  - `model/` - MongoDB document models
  - `repository/` - Data access layer for MongoDB
  - `service/` - Business logic layer
  - `controller/` - REST API endpoints
  - `config/` - Configuration classes

### MongoDB Data Model
```json
{
  "_id": "ObjectId",
  "orderId": "Long",
  "amount": "BigDecimal",
  "itemsCount": "Integer",
  "date": "ISODate",
  "customerName": "String",
  "processingTimeMs": "Long"
}
```

### Build Configuration
- Use Maven for build and dependency management.
- Include the following dependencies in the `pom.xml`:
  ```xml
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-mongodb</artifactId>
  </dependency>
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
  </dependency>
  ```

### Deployment
- Deploy this service independently of the Order Management Service.
- Use Docker for containerization and Kubernetes for orchestration if needed.

### Integration with Order Management Service
- The Order Management Service sends processed order data to this service via REST API.
- Ensure proper authentication and authorization between the services.

### Testing
- Write unit tests for service and repository layers.
- Use MockMvc for controller testing.
- Test MongoDB operations with an embedded MongoDB instance.

## Microservices Separation

In this project, the Order Management Service and the Order Processing Log Service are two distinct microservices. Each microservice has its own independent project structure, build configuration, and deployment pipeline.

### Order Management Service
- **Purpose**: Handles the lifecycle of orders, including creation, updates, and deletion.
- **Database**: Uses PostgreSQL for storing order and order line data.
- **Build Output**: Produces a single JAR file for deployment.
- **Endpoints**:
  - `/orders` for managing orders.

### Order Processing Log Service
- **Purpose**: Logs processed order data for auditing and analytics.
- **Database**: Uses MongoDB for storing log data.
- **Build Output**: Produces a separate JAR file for deployment.
- **Endpoints**:
  - `/logs` for receiving log data from the Order Management Service.

### Parent POM Configuration
- The parent `pom.xml` manages shared dependencies and configurations for both microservices.
- Each microservice has its own child `pom.xml` to define service-specific dependencies and configurations.

### Deployment
- Both microservices are independently deployable.
- Use Docker for containerization and Kubernetes for orchestration if needed.

### Example Project Structure
```
parent-pom/
├── pom.xml (parent POM)
├── order-management-service/
│   └── pom.xml (child POM for Order Management Service)
└── order-processing-log-service/
    └── pom.xml (child POM for Order Processing Log Service)
```

This separation ensures modularity, scalability, and independent deployment of each microservice.
