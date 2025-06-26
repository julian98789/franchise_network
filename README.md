# Franchise Network

`franchise_network` is a backend application developed in **Java 17** using **Spring WebFlux**, **R2DBC**, and **Gradle**, designed to manage franchises, branches, and the products assigned to each branch. The application follows a **hexagonal architecture (Ports & Adapters)**, promoting scalability, flexibility, and testability.

---

## Project Structure

The solution follows a hexagonal architecture, organizing the code into three main layers:

### 1. **Domain Layer**
- **Model:** Contains the core domain models such as `Franchise`, `Branch`, `Product`, etc.
- **Exceptions:** Defines business and technical exceptions.
- **Enums:** Includes enumerations for technical messages and error codes.
- **Usecase:** Contains the business use cases like `register`, `update`, etc.

### 2. **Infrastructure Layer**
- **Adapters:** Contains implementations that interact with external systems such as the database.
    - **Persistence:** Includes entities representing database tables, mappers for converting between entities and domain models, and the concrete implementations of persistence ports.
- **Entrypoints:** Contains the entry adapters to the system, primarily for exposing HTTP services.
    - Includes functional routers, handlers that orchestrate logic upon receiving requests, DTOs representing input/output data, and mappers that transform between DTOs and domain models.

### 3. **Application Layer**
- **Config:** Central configuration that orchestrates the creation of business use cases (`UseCases`) and the injection of their dependencies.

---

## Run Locally

### Prerequisites

- Java 17
- Docker
- IDE such as IntelliJ or VS Code

### 1. Clone the repository

```bash
git clone https://github.com/julian98789/franchise_network.git
cd franchise_network
```
### 2. Start MySQL database

```bash
docker run --name mysql-franchise \
  -e MYSQL_ROOT_PASSWORD=admin \
  -e MYSQL_DATABASE=franquicia \
  -e MYSQL_USER=admin \
  -e MYSQL_PASSWORD=admin \
  -p 3306:3306 \
  -d mysql:8.0
```

### 3. Configure the connection

```bash
spring.r2dbc.url="your url"
spring.r2dbc.username="your username"
spring.r2dbc.password="your password"
```

### 4. Run the application

```bash
./gradlew bootRun

