# Product Analytics System

A microservices-based event-driven system for tracking and analyzing product views using Apache Kafka and Kafka Streams.

## Services

### product-view-service
REST API microservice that tracks product views and publishes events to Kafka.
- **Tech:** Spring Boot, PostgreSQL, Kafka Producer
- **Port:** 9095
- **API:** `POST /product-view`

### product-analytics-service (Coming Soon)
Kafka Streams consumer that processes product view events and generates analytics.
- **Tech:** Spring Boot, Kafka Streams
- **Input Topic:** product-view
- **Output Topic:** product-trends

## Tech Stack

- Java 21
- Spring Boot 3.2.1
- Apache Kafka 4.1.1
- PostgreSQL
- Docker

## Getting Started

### Prerequisites
- Java 21
- PostgreSQL
- Apache Kafka
- Docker (optional)

### Setup

1. Create databases:
```bash
createdb product_view_db
```

2. Start Kafka (if not running):
```bash
./bin/kafka-server-start.sh ./config/server.properties
```

3. Start product-view-service:
```bash
cd product-view-service
mvn spring-boot:run
```

### API Usage

POST `/product-view`
```json
{
  "productId": 1,
  "productName": "Laptop Pro",
  "category": "Electronics",
  "deviceType": "DESKTOP",
  "userId": "user123"
}
```

## Architecture
```
REST API (product-view-service)
    ↓
PostgreSQL (persistence)
    ↓
Kafka Topic (product-view)
    ↓
Kafka Streams (product-analytics-service) [Coming Soon]
    ↓
Output Topic (product-trends)
```

## Key Features

- ✅ Async Kafka publishing
- ✅ Event-driven architecture
- ✅ Stateful stream processing (coming)
- ✅ Docker containerization
