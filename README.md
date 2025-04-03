# Transaction Management System

## Overview
This is a simple transaction management system built with Java 21 and Spring Boot 3.4.4. It allows users to create, modify, delete, and list transactions with various filtering options.

## Features
- Create, delete, and modify transactions
- List transactions with pagination and filtering
- In-memory repository for fast access
- Containerized with Docker and Kubernetes support

## Project Structure
- `controller`: RESTful API controllers and response handler 
- `config`: Application configurations
- `exception`: Custom exception handling
- `model`: Data models and enums
- `repository`: Data access layer
- `service`: Business logic
- `util`: Utility classes

## API Endpoints
- POST /api/transaction: Create a new transaction
- DELETE /api/transaction/{id}: Delete a transaction
- PUT /api/transaction/{id}: Modify a transaction
- GET /api/transaction/list: List all transactions with pagination and filters
- GET /api/transaction/{id}: Get a specific transaction



## External Libraries
- Spring Boot Web Starter - Provides support for RESTful API development
- Spring Boot Cache Starter - Enables method-level caching functionality
- Spring Boot Test Starter - Used for unit testing and integration testing

## Code Coverage
![coverage](https://markdown-1257179947.cos.ap-guangzhou.myqcloud.com/2025-0403-coverage-transaction-management.png)


## Other file or folder

```text
├── picture/       # Stores related images/charts
└── wrk/           # Root directory for wrk benchmarking tool
    ├── lua/       # Contains custom Lua scripts for dynamic testing
    └── result/    # Output directory for test reports
```

## How to Run
### Locally
```bash
mvn spring-boot:run
```
### Docker
```bash
docker build -t transaction-management .
docker run -p 8080:8080 transaction-management
```

### Kubernetes
```bash
kubectl apply -f k8s.yaml
```



