# E-Commerce Order Management System

A backend application for managing products, inventory, users, and customer orders.

I built this project with Java and Spring Boot to get hands-on experience with backend concepts beyond basic CRUD, 
including JWT authentication, role-based authorization, asynchronous processing, transaction management, 
and handling concurrent inventory updates.

## Tech Stack

- Java
- Spring Boot
- Spring Security
- Spring Data JPA / Hibernate
- MySQL
- JWT (JJWT)
- Maven
- Postman

## Features

- User registration and login
- JWT-based stateless authentication
- Role-based access for `CUSTOMER` and `ADMIN`
- Product management with pagination and filtering
- Inventory management
- Asynchronous order processing
- Transactional inventory updates
- Protection against concurrent overselling
- Order status management
- User profile management and account activation/deactivation
- Centralized exception handling
- Request validation

## API Overview

The application includes APIs for authentication, products, inventory, orders, user profiles, and admin operations.

-- Authentication

POST /api/auth/register - Register a new customer  
POST /api/auth/login - Login and receive a JWT token

-- Products

GET /api/products - Get all products  
GET /api/products/page - Get products with pagination  
GET /api/products/{id} - Get product by ID  
GET /api/products/search?category=... - Filter products by category  
GET /api/products/search?active=... - Filter products by active status  
POST /api/products - Create a product (Admin)  
PUT /api/products/{id} - Update a product (Admin)  
DELETE /api/products/{id} - Delete/deactivate a product (Admin)

-- Inventory

GET /api/inventory/product/{id} - Get inventory for a product  
PUT /api/inventory/product/{id} - Update inventory (Admin)

-- Orders

POST /api/orders - Create an order  
GET /api/orders/{id} - Get order by ID  
GET /api/orders/my-orders - Get current user's orders  
PATCH /api/orders/{id}/status - Update order status (Admin)

Orders are initially created with PENDING status and processed asynchronously. The create order API returns 
202 Accepted while order processing continues in the background.

-- User Profile

GET /api/users/me - Get current user's profile  
PATCH /api/users/me - Update current user's profile  
DELETE /api/users/me - Deactivate current user's account

-- Admin

GET /api/admin/orders - Get all orders  
GET /api/admin/users - Get all users  
GET /api/admin/users/{id} - Get user by ID  
PATCH /api/admin/users/{userId}/deactivate - Deactivate a customer  
PATCH /api/admin/users/{userId}/activate - Reactivate a customer

A few test endpoints are also included for testing authentication and role-based access during development.


## How Order Processing Works

Orders are processed asynchronously so inventory processing does not have to complete before the order request is 
accepted.

When a customer creates an order:

1. The authenticated user and requested products are validated.
2. The order is saved with `PENDING` status.
3. The database transaction commits.
4. An `OrderSubmittedEvent` is handled after the commit.
5. The order is submitted to a bounded `ExecutorService`.
6. A worker thread processes the inventory updates.
7. The order becomes `CONFIRMED` if processing succeeds.
8. If processing fails, inventory changes are rolled back and the order is marked `FAILED`.

```
POST /orders
     |
     v
Create PENDING Order
     |
     v
Database Commit
     |
     v
AFTER_COMMIT Event
     |
     v
Bounded ExecutorService
     |
     v
Process Inventory
   /       \
Success    Failure
  |           |
  v           v
CONFIRMED   Rollback
              |
              v
            FAILED
```

The API returns `202 Accepted` when the order has been accepted for processing. The final order status can then be
retrieved through the order API.

## Inventory and Concurrency

Multiple orders can be processed by different worker threads at the same time, so checking the stock in Java 
before updating it could allow two requests to purchase the same remaining inventory.

To avoid this, stock is decreased using an atomic conditional database update:

```sql
UPDATE inventory
SET quantity = quantity - :requestedQuantity
WHERE product_id = :productId
  AND quantity >= :requestedQuantity;
```

The affected row count determines whether the update succeeded.

If the result is `0`, there was not enough stock and order processing fails.

The complete inventory operation is also transactional. For an order containing multiple products, 
if one inventory update fails, the earlier updates for that order are rolled back as well.

## Asynchronous Processing

Order processing uses a bounded `ThreadPoolExecutor` with:

- 5 worker threads
- Queue capacity of 100 tasks
- `CallerRunsPolicy` for backpressure when the executor is saturated

The order-processing event is handled with `@TransactionalEventListener(phase = AFTER_COMMIT)`.

This ensures asynchronous processing starts only after the initial order transaction has successfully committed.

A separate `REQUIRES_NEW` transaction is used when updating the final order status. This allows an order to be 
marked `FAILED` even when its inventory-processing transaction has been rolled back.

## Authentication and Authorization

The application uses JWT-based stateless authentication.

After a successful login, the server generates a signed JWT containing the user's email. Protected requests send 
the token using:

```
Authorization: Bearer <token>
```

A custom JWT authentication filter validates the token and loads the authenticated user into Spring Security's 
`SecurityContext`.

Access is controlled using two roles:

- `CUSTOMER` — browse products, view inventory, manage their profile, and create/view their orders
- `ADMIN` — manage products and inventory, manage users, view orders, and update order statuses

Passwords are stored using BCrypt hashing.

## Order Status

Orders can have the following statuses:

```
PENDING
CONFIRMED
SHIPPED
DELIVERED
CANCELLED
FAILED
```

Supported transitions include:

```
PENDING -----> CONFIRMED -----> SHIPPED -----> DELIVERED
   |               |
   |               |
   +-> CANCELLED <-+
   |
   +-> FAILED
```

Invalid status transitions are rejected by the application.

## Error Handling

The project uses centralized exception handling to return consistent API error responses.

Example:

```json
{
  "message": "Order not found with id: 100",
  "status": 404,
  "timestamp": "..."
}
```

Validation errors, missing resources, insufficient inventory, authentication failures, authorization failures, 
and invalid order operations are handled with appropriate HTTP responses.

## Configuration

Database credentials and the JWT signing secret are not stored in the repository.

The application expects the following environment variables:

```
DB_PASSWORD=<your-mysql-password>
JWT_SECRET=<your-jwt-secret>
```

For HMAC JWT signing, use a sufficiently long random secret. The configured key must meet the minimum size 
required by the selected HMAC algorithm.

The application configuration references these variables:

```yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ecommerce_order_mgmt
    username: root
    password: ${DB_PASSWORD}

jwt:
  secret: ${JWT_SECRET}
```

## Running the Project

### Prerequisites

Make sure you have:

- Java installed
- Maven
- MySQL
- Postman or another API client

Create the database:

```sql
CREATE DATABASE ecommerce_order_mgmt;
```

Set `DB_PASSWORD` and `JWT_SECRET` in your environment or IDE run configuration.

Then run the application:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

## Testing

The APIs were tested with Postman for both successful and failure scenarios, including:

- registration and login
- JWT authentication
- customer/admin authorization
- product and inventory operations
- successful asynchronous order processing
- insufficient inventory
- transaction rollback
- invalid order status transitions
- user activation/deactivation
- validation and missing-resource errors

One concurrency test submitted multiple orders in quick succession and verified that different executor worker 
threads processed the orders concurrently.

## Project Structure

```
src/main/java/com/ecommerce
|
|-- config
|-- controller
|-- dto
|   |-- request
|   `-- response
|-- event
|-- exception
|-- model
|-- repository
|-- security
`-- service
```

## What I Learned

This project started as an e-commerce REST API, but the most useful part was working through 
what happens when order processing becomes concurrent.

It helped me understand the difference between simply using `@Transactional` and actually preventing concurrent 
stock updates, why asynchronous work should start after the original transaction commits, and why failure 
status updates sometimes need their own transaction.

I also got practical experience with Spring Security's authentication flow, JWT validation, JPA transaction 
boundaries, exception handling, and designing APIs around asynchronous operations.