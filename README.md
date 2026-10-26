# Microservices Order System (Java + Spring Boot)

**What happens to an online shop when one part of it breaks?**

I built an order system as **small separate services** instead of one big program.
Each service does one job and can be started, stopped, updated and scaled on its own.
When one service goes down, the rest keep working and the customer gets a clear message.

---

## The parts

```
                    ┌──────────────────┐
  Client  ───────▶  │   API Gateway    │  port 8080 (the single front door)
                    └────────┬─────────┘
                             │  finds services by name in Eureka
             ┌───────────────┼────────────────┬─────────────────┐
             ▼               ▼                ▼                 ▼
      ┌────────────┐  ┌─────────────┐  ┌─────────────┐  ┌──────────────┐
      │   Order    │  │  Inventory  │  │   Payment   │  │   Shipping   │
      │  service   │─▶│   service   │  │   service   │  │   service    │
      │   8081     │─▶│    8082     │  │    8083     │  │     8084     │
      └────────────┘─▶└─────────────┘  └─────────────┘  └──────────────┘
         (the order service calls the other three with Feign)

      ┌──────────────────┐        ┌──────────────┐
      │ Discovery server │        │  PostgreSQL  │  one database per service
      │ (Eureka) 8761    │        └──────────────┘
      └──────────────────┘
```

| Part | What it does |
|---|---|
| **API Gateway** (API = Application Programming Interface, the way programs talk to each other) | The one door all requests come through. It sends each request to the right service. |
| **Discovery server (Eureka)** | A "phone book". Every service writes its address here, and others look it up by name. |
| **Order service** | Runs the order: reserve stock, take payment, create shipment. |
| **Inventory service** | Knows how much stock there is. Reserves and releases stock. |
| **Payment service** | A pretend card payment. It declines anything above R50,000 (a simple limit rule). |
| **Shipping service** | Creates a shipment with a tracking number. |

---

## How an order works

1. **Reserve stock.** If there is not enough, the order is `OUT_OF_STOCK` and stops here.
2. **Take payment.** If the payment is declined, the stock is **put back** and the order is `PAYMENT_DECLINED`.
3. **Create the shipment.** The order is `CONFIRMED` with a tracking number.

---

## What happens when something breaks

This is the main point of the project. Each service call has a **circuit breaker**.

**What is a circuit breaker?** It works like the trip switch in your house. If a service keeps failing, the circuit "opens" and we stop calling it for 30 seconds. Instead, we use a safe backup answer (a **fallback**). This stops one broken service from slowing down or crashing everything else.

I decided what each failure should mean for the business:

| If this is down... | ...this happens | Why |
|---|---|---|
| **Inventory** | The order is stopped. The customer gets HTTP 503 (HTTP = HyperText Transfer Protocol, the language of the web; 503 means "service unavailable") "try again later". | We must not sell stock we cannot check. |
| **Payment** | The reserved stock is **put back**, and the customer gets 503. | Stock must not stay locked for an order that was never paid. |
| **Shipping** | The order is **kept** as `CONFIRMED_SHIPPING_PENDING`. | The customer already paid. Shipping can be done a bit later. |
| **Order service** | The gateway's circuit breaker answers: "The order service is busy or down. Please try again in a moment." | The client gets a clear answer instead of a long wait. |

---

## I tested it

**10 automatic tests** (JUnit, the standard Java testing tool), all passing. The order flow tests use fake (mock) services to check every path: happy path, out of stock, payment declined, payment service down, shipping service down.

**A full run** with all 6 services. I sent real orders through the gateway, then switched services off one by one. These are the real answers I got (I also ran the normal order again with PostgreSQL to check the database setup):

```
Normal order       → {"status":"CONFIRMED","trackingNumber":"MZFD8DFC24", "totalAmount":9000}
Too many laptops   → {"status":"OUT_OF_STOCK","note":"Not enough stock for LAPTOP-01"}
R60,000 order      → {"status":"PAYMENT_DECLINED"}   (stock went back from 5 to 10)
Shipping off       → {"status":"CONFIRMED_SHIPPING_PENDING","note":"Paid. Shipping service was down, ..."}
Inventory off      → HTTP 503 "Service Unavailable"
Order service off  → HTTP 503 {"message":"The order service is busy or down. Please try again in a moment."}
```

---

## How to run it

**Option 1: on your computer (no database to install).**
Each service uses an in-memory H2 database (a small database that lives inside the program, so you install nothing) by default.

```bash
mvn package
java -jar discovery-server/target/discovery-server.jar      # start this first
java -jar inventory-service/target/inventory-service.jar
java -jar payment-service/target/payment-service.jar
java -jar shipping-service/target/shipping-service.jar
java -jar order-service/target/order-service.jar
java -jar api-gateway/target/api-gateway.jar
```

**Option 2: with Docker and PostgreSQL.**

```bash
mvn package
docker compose up --build
```

**Place an order** (wait about 30 seconds after starting, so the services can find each other):

```bash
curl -X POST localhost:8080/api/orders -H "Content-Type: application/json" \
  -d '{"sku":"PHONE-01","quantity":2,"unitPrice":4500,"address":"12 Main Road, Thohoyandou"}'
```

See the stock: `curl localhost:8080/api/inventory` · See all orders: `curl localhost:8080/api/orders` · Eureka dashboard: http://localhost:8761

---

## Trade-offs (what microservices cost)

Microservices are not free. Being honest about this matters:
- **More moving parts.** Six programs to run and watch instead of one.
- **More network calls.** Each call can be slow or fail, so every call needs a timeout and a fallback.
- **More places to secure.** That is one reason for the gateway: one front door is easier to protect and monitor.
- **No single transaction across services.** If payment fails, the order service has to put the stock back itself.

For a small shop, one well-built program would often be the better choice. Microservices make sense when different parts need to change or scale at different speeds.

## What I would add next

- Login and access checks at the gateway (for example with OAuth2, an open standard for logins, and JWT, JSON Web Tokens).
- A message queue (like Kafka) so shipping can retry by itself when it comes back.
- Tracing, so one order can be followed across all services in the logs.

---

## Files

```
├── discovery-server/   # Eureka
├── api-gateway/        # Spring Cloud Gateway + circuit breaker fallback
├── order-service/      # order flow, Feign clients, fallbacks, tests
├── inventory-service/
├── payment-service/
├── shipping-service/
├── docker/init-databases.sql   # one PostgreSQL database per service
├── docker-compose.yml
├── Dockerfile          # one Dockerfile for all services
└── pom.xml             # parent Maven project
```

## Note

I built this project from the Java backend study guide I wrote for myself, based on a real job ad. I first built it in 2026 on a laptop that later broke, so I rebuilt it and put it here.

**Tools:** Java 17 · Spring Boot 3.4 · Spring Cloud (Gateway, Eureka, OpenFeign, Resilience4j) · Spring Data JPA (Java Persistence API, how Java saves objects to a database) · PostgreSQL · H2 · Docker · JUnit 5 · Mockito · Maven

---

*Rolivhuwa Thomoli*
