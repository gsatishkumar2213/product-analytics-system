# Real-Time Product Analytics with Kafka Streams

A microservices system demonstrating advanced Kafka Streams patterns for real-time event processing
and enrichment.

## What It Does

Tracks product views and performs real-time analytics using Kafka Streams:

- **Windowed Aggregations**: Count views per product in 5-minute windows
- **Stream-Table Joins**: Enrich view events with product catalog data
- **Multiple Topologies**: Parallel processing paths from same input stream

## Architecture

```
Product View API → Kafka → Kafka Streams → Analytics
                              ├─> Windowed counts
                              └─> Enriched events (with product details)
```

## Example: Stream-Table Join

**Input Event:**

```json
{
  "productId": 1,
  "userId": "user123",
  "viewedAt": "2026-02-08T21:10:03"
}
```

**After Join (enriched with catalog data):**

```json
{
  "productId": 1,
  "name": "Wireless Mouse",
  "price": 29.99,
  "userId": "user123",
  "viewedAt": "2026-02-08T21:10:03"
}
```

## Tech Stack

Java 21 • Spring Boot • Kafka Streams • PostgreSQL

## Key Patterns Demonstrated

- KStream vs KTable semantics
- Stateful aggregations with time windows
- Stream-table joins for enrichment
- Custom JSON Serdes
- Programmatic topic creation (AdminClient)

## Quick Start

1. **Start Kafka:**

```bash
   cd kafka_2.13-4.1.1
   ./bin/kafka-server-start.sh ./config/kraft/server.properties
```

2. **Create database:**

```bash
   createdb product_view_db
```

3. **Run services:**

```bash
   # Terminal 1: Analytics Service
   cd product-analytics-service && mvn spring-boot:run
   
   # Terminal 2: View Service
   cd product-view-service && mvn spring-boot:run
```

4. **Test the system:**

```bash
   curl -X POST http://localhost:9095/product-view \
     -H "Content-Type: application/json" \
     -d '{
       "productId": 1,
       "productName": "Test Product",
       "category": "Electronics",
       "deviceType": "DESKTOP",
       "userId": "user123"
     }'
```

5. **View enriched events:**

```bash
   ./bin/kafka-console-consumer.sh \
     --bootstrap-server localhost:9092 \
     --topic enriched-product-view \
     --from-beginning \
     --property print.key=true
```

## Services

**product-view-service** (Port 9095): REST API for tracking product views  
**product-analytics-service** (Port 9096): Kafka Streams real-time processing

## What I Learned

- Real-time event enrichment patterns
- Windowing and stateful stream processing
- Join semantics in distributed systems
- Event-driven microservices architecture