# E-Commerce Microservices Application

A full-featured e-commerce platform built with Spring Boot microservices architecture, providing scalable and distributed services for online shopping operations.

## Architecture Overview

This application follows a microservices architecture pattern with the following services:

- **Gateway Server** - API Gateway with routing and security
- **Eureka Server** - Service discovery and registration
- **Products Service** - Product catalog management
- **Carts Service** - Shopping cart operations
- **Orders Service** - Order processing and management
- **Payments Service** - Payment processing with Stripe integration
- **Users Service** - User management and authentication
- **Shippings Service** - Shipping and delivery management
- **Message Service** - Notification and messaging

## Technology Stack

- **Java 21** - Programming language
- **Spring Boot 3.4.3** - Application framework
- **Spring Cloud 2024.0.0** - Microservices framework
- **Netflix Eureka** - Service discovery
- **Spring Cloud Gateway** - API Gateway
- **OpenFeign** - Declarative REST client
- **Spring Data JPA** - Data persistence
- **H2 Database** - In-memory database
- **Keycloak** - OAuth2/OpenID Connect authentication
- **JWT** - Token-based authentication
- **Stripe API** - Payment processing
- **Spring Actuator** - Monitoring and health checks
- **Lombok** - Code generation
- **Maven** - Build tool

## Key Features

### Service Discovery & Registration
- Automatic service registration with Eureka
- Dynamic service discovery
- Load balancing across service instances

### API Gateway
- Centralized routing to microservices
- OAuth2 resource server with JWT validation
- Keycloak integration for authentication
- Role-based access control
- Custom rate limiting with Redis
- Security filters for authorization

### Product Management
- Product catalog with categories
- Inventory management
- Stock tracking
- Product search and filtering

### Shopping Cart
- Add/remove items from cart
- Cart persistence per user
- Real-time stock validation
- Integration with Products service via Feign

### Order Processing
- Order creation from cart
- Order status tracking
- Order history
- Integration with multiple services

### Payment Integration
- Stripe payment gateway integration
- Secure payment processing
- Payment confirmation workflow
- Transaction tracking

### User Management
- User registration and authentication
- User profile management
- JWT-based authorization
- Role-based permissions

### Monitoring & Observability
- Spring Actuator endpoints
- Health checks for all services
- Service metrics and info endpoints
- Centralized logging

### Inter-Service Communication
- Synchronous communication via OpenFeign
- Service-to-service authentication
- Resilient communication patterns

## Service Ports

| Service | Port |
|---------|------|
| Eureka Server | 8070 |
| Products | 8080 |
| Carts | 8090 |
| Gateway | 9050 |
| Orders | 9010 |
| Payments | 9030 |
| Users | 9040 |
| Shippings | (configured) |
| Message | (configured) |

## Prerequisites

- Java 21 or higher
- Maven 3.6+
- Keycloak server running on port 7080
- Stripe account (for payment processing)

## Configuration

### Environment Variables

Set the following environment variables for Payments service:
```bash
STRIPE_SECRET_KEY=your_stripe_secret_key
STRIPE_PUBLISHABLE_KEY=your_stripe_publishable_key
```

### Keycloak Setup

Configure Keycloak realm at `http://localhost:7080/realms/master` with appropriate clients and roles.

## Running the Application

1. Start Eureka Server:
```bash
cd EurekaServer
mvn spring-boot:run
```

2. Start Gateway Server:
```bash
cd GatewayServer
mvn spring-boot:run
```

3. Start individual microservices:
```bash
cd [ServiceName]
mvn spring-boot:run
```

## API Access

All API requests should go through the Gateway Server at `http://localhost:9050`

Example endpoints:
- Products: `http://localhost:9050/products/**`
- Carts: `http://localhost:9050/carts/**`
- Orders: `http://localhost:9050/orders/**`
- Payments: `http://localhost:9050/payments/**`

## Monitoring

- Eureka Dashboard: `http://localhost:8070`
- H2 Console: `http://localhost:[service-port]/h2-console`
- Actuator Health: `http://localhost:[service-port]/actuator/health`
- Gateway Actuator: `http://localhost:9050/actuator/gateway/routes`

## Security

- OAuth2/OpenID Connect with Keycloak
- JWT token validation at Gateway level
- Role-based authorization
- Secure inter-service communication

## License

This project is for educational and demonstration purposes.
