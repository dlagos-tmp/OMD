# Simple Deployment and Testing Guide for the Order Management System

## Prerequisites
- Java 8
- Maven
- Docker and Docker Compose

## Deployment Steps

### 1. Clone the repository
```bash
git clone https://github.com/dlagos-tmp/OMD.git
cd OMD
```

### 2. Checkout the main branch
```bash
git checkout main
```

### 3. Build the application
```bash
mvn clean install
```
This will:
- Compile the code
- Run tests
- Package the application
- The Spring Boot Maven plugin will automatically repackage the JAR with the main class in the manifest

### 4. Deploy with Docker Compose
```bash
docker-compose -f docker-compose.yml -p last_1 up
```
This will:
- Start PostgreSQL and MongoDB containers
- Deploy both microservices (Order Management Service and Order Processing Log Service)

> **Note:** There's no need for manual SQL initialization scripts since Spring Data JPA with Hibernate automatically creates the database schema based on entity classes (using `spring.jpa.hibernate.ddl-auto=update`).

### 5. Access the application
Once deployed, the services will be available at:
- Order Management Service: http://localhost:8080
- Order Processing Log Service: http://localhost:8090

### 6. Test the API with Swagger UI
Open your browser and navigate to:
```
http://localhost:8080/swagger-ui/index.html
```
This interactive documentation allows you to:
- View all available endpoints
- Test API operations
- See request/response formats

Post example:
``` json
{
"customerName": "Dimitris",
"orderDate": "2025-08-08T09:55:06.863Z",
"orderLines": [
{
"productId": 10,
"quantity": 20,
"price": 20
},
{
"productId": 30,
"quantity": 20,
"price": 20
}
]
}
```
PUT example:

CASE 1:
First run GET for the Order you need to update, copy the response json.
GET_RESPONSE:
``` json
{
"orderId": 3,
"customerName": "Dimitris",
"status": "processed",
"orderDate": "2025-08-08T11:12:19.75",
"orderLines": [
{
"id": 3,
"productId": 10,
"quantity": 20,
"price": 20,
"orderId": 3
},
{
"id": 4,
"productId": 30,
"quantity": 20,
"price": 20,
"orderId": 3
}
]
}
```

Let's assume you need to remove orderLine 4 and add also a new OrderLine while changing the status to unprocessed. Notice that we have removed the "id": 4 OrderLine from the request, kept the "id": 3 and added a new one with no id field. Send the following body:
``` json
{
"customerName": "Dimitris",
"status": "unprocessed",
"orderDate": "2025-08-08T11:12:19.75",
"orderLines": [
{
"id": 3,
"productId": 10,
"quantity": 20,
"price": 20
},
{
"productId": 110,
"quantity": 70,
"price": 16
}
]
}
```
CASE 2:
First run GET for the Order you need to update, copy the response json.
GET_RESPONSE:
``` json
{
"orderId": 3,
"customerName": "Dimitris",
"status": "unprocessed",
"orderDate": "2025-08-08T11:12:19.75",
"orderLines": [
{
"id": 3,
"productId": 10,
"quantity": 20,
"price": 20,
"orderId": 3
},
{
"id": 6,
"productId": 110,
"quantity": 70,
"price": 16,
"orderId": 3
}
]
}
```
Let's assume you need to just update the quantity of the products:
Send the following body:
``` json
{
"customerName": "Dimitris",
"status": "unprocessed",
"orderDate": "2025-08-08T11:12:19.75",
"orderLines": [
{
"id": 3,
"productId": 10,
"quantity":9,
"price": 20,
"orderId": 3
},
{
"id": 6,
"productId": 110,
"quantity": 7,
"price": 16,
"orderId": 3
}
]
}
```