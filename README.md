# Spring Boot OpenTelemetry Application

A Spring Boot 4.0 application demonstrating distributed tracing, metrics, and logging using OpenTelemetry with Grafana LGTM stack (Loki, Grafana, Tempo, Mimir).

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [Observability](#observability)
- [Troubleshooting](#troubleshooting)
- [Project Structure](#project-structure)

## 🎯 Overview

This project demonstrates how to integrate OpenTelemetry with a Spring Boot application to collect traces, metrics, and logs, and visualize them using Grafana's observability stack. The application automatically instruments HTTP requests and exports telemetry data to an OpenTelemetry collector.

## ✨ Features

- **Distributed Tracing**: Automatic HTTP request tracing with OpenTelemetry
- **Metrics Collection**: Application metrics exported via OTLP
- **Structured Logging**: Logs exported to OpenTelemetry with Logback appender
- **Observability Stack**: Complete LGTM stack (Loki, Grafana, Tempo, Mimir) via Docker Compose
- **100% Sampling**: All traces are captured for development/testing
- **Service Identification**: Automatic service name configuration

## 🛠 Tech Stack

- **Java**: 21
- **Spring Boot**: 4.0.1
- **OpenTelemetry**: 1.55.0 (via Spring Boot starter)
- **Grafana LGTM Stack**: Latest
  - Grafana (UI & Dashboards)
  - Tempo (Distributed Tracing)
  - Loki (Log Aggregation)
  - Prometheus (Metrics)
  - OpenTelemetry Collector

## 📦 Prerequisites

- Java 21 or higher
- Maven 3.6+ (or use included Maven wrapper)
- Docker and Docker Compose
- At least 4GB of free RAM (for Docker containers)

## 🏗 Architecture

```
┌─────────────────┐
│  Spring Boot    │
│   Application   │──┐
│   (Port 8080)   │  │
└─────────────────┘  │
                     │  OTLP/HTTP
                     │  (Port 4318)
                     ▼
         ┌───────────────────────┐
         │  OpenTelemetry        │
         │  Collector            │
         └───────────────────────┘
                     │
         ┌───────────┼───────────┐
         │           │           │
         ▼           ▼           ▼
    ┌────────┐  ┌────────┐  ┌────────┐
    │ Tempo  │  │ Loki   │  │ Prom   │
    │(Traces)│  │(Logs)  │  │(Metrics)│
    └────────┘  └────────┘  └────────┘
         │           │           │
         └───────────┼───────────┘
                     │
                     ▼
              ┌──────────┐
              │ Grafana  │
              │(Port 3000)│
              └──────────┘
```

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd spring_opentelemetry
```

### 2. Start the Observability Stack

The project includes Docker Compose configuration that automatically starts the Grafana LGTM stack when you run the Spring Boot application (via `spring-boot-docker-compose` dependency).

Alternatively, you can start it manually:

```bash
docker compose up -d
```

This will start:
- **Grafana**: http://localhost:3000 (admin/admin)
- **OTLP HTTP Endpoint**: http://localhost:4318
- **OTLP gRPC Endpoint**: http://localhost:4317

### 3. Build the Application

```bash
# Using Maven wrapper (recommended)
./mvnw clean install

# Or using system Maven
mvn clean install
```

### 4. Run the Application

```bash
# Using Maven wrapper
./mvnw spring-boot:run

# Or using system Maven
mvn spring-boot:run
```

The application will start on **http://localhost:8080**

## ⚙️ Configuration

### Application Configuration (`application.yaml`)

```yaml
spring:
  application:
    name: spring_opentelemetry
  opentelemetry:
    resource-attributes:
      service.name: ${spring.application.name}
    tracing:
      export:
        otlp:
          endpoint: http://localhost:4318
    metrics:
      export:
        otlp:
          endpoint: http://localhost:4318
    logging:
      export:
        otlp:
          endpoint: http://localhost:4318

management:
  tracing:
    sampling:
      probability: 1.0  # 100% sampling for development
```

### Key Configuration Points

- **Service Name**: Set via `spring.opentelemetry.resource-attributes.service.name`
- **OTLP Endpoint**: Points to OpenTelemetry Collector on port 4318 (HTTP)
- **Sampling Rate**: Set to 1.0 (100%) to capture all traces
- **Logging**: Integrated with OpenTelemetry via Logback appender

### Docker Compose (`compose.yaml`)

```yaml
services:
  grafana-lgtm:
    image: 'grafana/otel-lgtm:latest'
    ports:
      - '3000:3000'    # Grafana UI
      - '4317:4317'    # OTLP gRPC
      - '4318:4318'    # OTLP HTTP
```

## 🏃 Running the Application

### Option 1: Maven Wrapper (Recommended)

```bash
./mvnw spring-boot:run
```

### Option 2: Run JAR

```bash
./mvnw clean package
java -jar target/springotel-0.0.1-SNAPSHOT.jar
```

### Option 3: IDE

Run `SpringOpentelemetryApplication` main class from your IDE.

## 📡 API Endpoints

The application provides the following REST endpoints:

### GET `/`
Returns a simple "home" message.

**Example:**
```bash
curl http://localhost:8080/
```

**Response:**
```
home
```

### GET `/welcome/{name}`
Returns a personalized welcome message.

**Example:**
```bash
curl http://localhost:8080/welcome/John
```

**Response:**
```
welcome John
```

### GET `/process`
Simulates a long-running process (5 seconds) for testing trace duration.

**Example:**
```bash
curl http://localhost:8080/process
```

**Response:**
```
processed
```

You can also use the included `client.http` file with HTTP client extensions (VS Code REST Client, IntelliJ HTTP Client, etc.)

## 📊 Observability

### Accessing Grafana

1. Open http://localhost:3000 in your browser
2. Login with:
   - **Username**: `admin`
   - **Password**: `admin`

### Viewing Traces

1. Navigate to **Explore** (compass icon in left sidebar)
2. Select **tempo** as the data source
3. Use TraceQL query:
   ```
   {resource.service.name="spring_opentelemetry"}
   ```
4. Set time range (e.g., "Last 30 minutes")
5. Click **Run query**

### Alternative: Grafana Explore Traces App

Use this URL to access the traces explorer:
```
http://localhost:3000/a/grafana-exploretraces-app/explore?from=now-30m&to=now&var-ds=tempo&var-groupBy=resource.service.name&actionView=traceList
```

**Note**: Remove `var-primarySignal` parameter if the trace table appears empty.

### Viewing Metrics

1. Navigate to **Explore**
2. Select **Prometheus** as the data source
3. Query metrics like:
   - `http_server_request_duration_seconds`
   - `http_server_requests_total`

### Viewing Logs

1. Navigate to **Explore**
2. Select **Loki** as the data source
3. Use LogQL query:
   ```
   {job="spring_opentelemetry"}
   ```

## 🔍 Useful TraceQL Queries

TraceQL is the query language used by Grafana Tempo to search and filter traces. Here are some useful queries for working with your Spring Boot OpenTelemetry application:

**Basic example to get all traces for your service:**
```traceql
{resource.service.name="spring_opentelemetry"}
```

### Basic Queries

**Get all traces for your service:**
```traceql
{resource.service.name="spring_opentelemetry"}
```

**Get traces for a specific operation:**
```traceql
{resource.service.name="spring_opentelemetry" && name="http get /process"}
```

**Get traces with duration filter (longer than 1 second):**
```traceql
{resource.service.name="spring_opentelemetry" && duration > 1s}
```

**Get traces with status code filter:**
```traceql
{resource.service.name="spring_opentelemetry" && status.code = 200}
```

### Advanced Queries

**Find traces with errors:**
```traceql
{resource.service.name="spring_opentelemetry" && status.code >= 400}
```

**Find traces by HTTP method:**
```traceql
{resource.service.name="spring_opentelemetry" && http.method = "GET"}
```

**Find traces by URI pattern:**
```traceql
{resource.service.name="spring_opentelemetry" && http.url =~ "/welcome/.*"}
```

**Combine multiple conditions:**
```traceql
{resource.service.name="spring_opentelemetry" && http.method = "GET" && duration > 100ms && status.code = 200}
```

**Find traces with specific outcome:**
```traceql
{resource.service.name="spring_opentelemetry" && http.status_code >= 200 && http.status_code < 300}
```

### Query by Attributes

**Filter by custom span attributes:**
```traceql
{resource.service.name="spring_opentelemetry" && method = "GET"}
```

**Filter by URI:**
```traceql
{resource.service.name="spring_opentelemetry" && uri = "/process"}
```

**Filter by outcome:**
```traceql
{resource.service.name="spring_opentelemetry" && outcome = "SUCCESS"}
```

### Useful Patterns

**Get the slowest traces:**
```traceql
{resource.service.name="spring_opentelemetry"} | duration > 5s
```

**Get traces from last 5 minutes:**
```traceql
{resource.service.name="spring_opentelemetry"} | now() - timestamp < 5m
```

**Find all services (no filter):**
```traceql
{}
```

**Find traces matching any service name pattern:**
```traceql
{resource.service.name =~ ".*"}
```

### TraceQL Operators

- `=` - Exact match
- `!=` - Not equal
- `=~` - Regular expression match
- `!~` - Regular expression not match
- `>` - Greater than
- `>=` - Greater than or equal
- `<` - Less than
- `<=` - Less than or equal
- `&&` - Logical AND
- `||` - Logical OR

### Example Use Cases

**Find slow process endpoints:**
```traceql
{resource.service.name="spring_opentelemetry" && name =~ ".*process.*" && duration > 3s}
```

**Find all successful requests:**
```traceql
{resource.service.name="spring_opentelemetry" && status.code = 200 && outcome = "SUCCESS"}
```

**Find traces by welcome endpoint:**
```traceql
{resource.service.name="spring_opentelemetry" && uri =~ "/welcome/.*"}
```

For more information, see the [TraceQL documentation](https://grafana.com/docs/tempo/latest/traceql/).

## 🔧 Troubleshooting

### Traces Not Appearing in Grafana

**Problem**: Traces count shows but table is empty.

**Solution**:
1. Remove the `var-primarySignal` parameter from the Grafana URL
2. Use TraceQL query directly: `{resource.service.name="spring_opentelemetry"}`
3. Expand the time range to ensure traces are included

See `GRAFANA_TRACE_FIX.md` for detailed troubleshooting.

### Docker Containers Not Starting

**Problem**: Docker Compose fails to start containers.

**Solution**:
```bash
# Check Docker is running
docker ps

# Check port availability
netstat -ano | findstr "3000 4317 4318"

# Start containers manually
docker compose up -d

# Check logs
docker compose logs grafana-lgtm
```

### Application Cannot Connect to OTLP Endpoint

**Problem**: Application fails to send traces.

**Solution**:
1. Verify Grafana LGTM stack is running:
   ```bash
   docker ps | grep grafana-lgtm
   ```
2. Check OTLP endpoint is accessible:
   ```bash
   curl http://localhost:4318
   ```
3. Verify `application.yaml` endpoint configuration matches Docker Compose ports

### Traces Are Empty or Missing Attributes

**Problem**: Traces appear but lack service name or other attributes.

**Solution**:
1. Verify `spring.opentelemetry.resource-attributes.service.name` is set
2. Restart the application after configuration changes
3. Check application logs for OpenTelemetry export errors

## 📁 Project Structure

```
spring_opentelemetry/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/springotel/
│   │   │       ├── config/
│   │   │       │   └── ConfigOpenTelemetryAppender.java
│   │   │       ├── controller/
│   │   │       │   └── RequestController.java
│   │   │       └── SpringOpentelemetryApplication.java
│   │   └── resources/
│   │       ├── application.yaml          # Application configuration
│   │       ├── logback-spring.xml        # Logback with OpenTelemetry
│   │       └── static/
│   └── test/
│       └── java/
│           └── com/springotel/
│               └── SpringOpentelemetryApplicationTests.java
├── compose.yaml                           # Docker Compose for Grafana LGTM
├── pom.xml                                # Maven dependencies
├── client.http                            # HTTP client test requests
├── GRAFANA_TRACE_FIX.md                   # Troubleshooting guide
└── README.md                              # This file
```

## 🔍 Key Files

- **`application.yaml`**: Main configuration file for Spring Boot and OpenTelemetry
- **`compose.yaml`**: Docker Compose configuration for observability stack
- **`logback-spring.xml`**: Logback configuration with OpenTelemetry appender
- **`ConfigOpenTelemetryAppender.java`**: Spring component that installs OpenTelemetry appender for logback
- **`RequestController.java`**: REST controller with example endpoints
- **`client.http`**: HTTP client requests for testing endpoints

## 📚 Additional Resources

- [Spring Boot OpenTelemetry Documentation](https://docs.spring.io/spring-boot/4.0.1/reference/actuator/observability.html#actuator.observability.opentelemetry)
- [OpenTelemetry Java Documentation](https://opentelemetry.io/docs/instrumentation/java/)
- [Grafana Tempo Documentation](https://grafana.com/docs/tempo/latest/)
- [TraceQL Query Language](https://grafana.com/docs/tempo/latest/traceql/)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## 📝 License

[Add your license information here]

## 👤 Author

[Add your information here]

---

**Happy Observability! 🎉**

