# Wali

**Wali** is an electronic wallet platform built on a **microservices** architecture.  
It enables distributed management of users, wallets, transactions, and notifications.

***

## Architecture

Wali is developed using a **microservices** architecture primarily based on **Spring Boot**, **PostgreSQL**, and **Apache Kafka**.

```text
                           ┌──────────────────┐
                           │      Client      │
                           └────────┬─────────┘
                                    │
                                    ▼
                           ┌──────────────────┐
                           │   API Gateway    │
                           └────────┬─────────┘
                                    │
              ┌─────────────────────┼─────────────────────┐
              │                     │                     │
              ▼                     ▼                     ▼
       ┌──────────────┐      ┌──────────────┐      ┌──────────────┐
       │ Auth Service │      │Wallet Service│      │ Transaction  │
       │    :8001     │      │    :8002     │      │   Service    │
       └──────┬───────┘      └──────┬───────┘      │    :8003     │
              │                     │              └──────┬───────┘
              ▼                     ▼                     │
        ┌───────────┐         ┌───────────┐               │
        │PostgreSQL │         │PostgreSQL │               │
        │ Auth DB   │         │ Wallet DB │               │
        └───────────┘         └───────────┘               │
                                                          │
                                                          ▼
                                                   ┌─────────────┐
                                                   │    Kafka    │
                                                   └──────┬──────┘
                                                          │
                                                          ▼
                                               ┌──────────────────┐
                                               │Notification      │
                                               │Service :8004     │
                                               └────────┬─────────┘
                                                        │
                                                        ▼
                                                  ┌───────────┐
                                                  │PostgreSQL │
                                                  │Notification│
                                                  └───────────┘
```

***

## Microservices

| Service                |   Port | Responsibility                                              |
| ---------------------- | -----: | ----------------------------------------------------------- |
| `auth-service`         | `8001` | Authentication, registration, and user management           |
| `wallet-service`       | `8002` | Wallet and balance management                               |
| `transaction-service`  | `8003` | Transaction creation and processing                         |
| `notification-service` | `8004` | Notification management and delivery                        |

Each microservice has its own database to comply with the **Database per Service** principle.

***

## Technologies

### Backend

* Java 17
* Spring Boot
* Spring Security
* JWT
* Spring Data JPA / Hibernate
* Apache Kafka
* PostgreSQL

### Testing

* JUnit
* Mockito
* Spring Boot Test
* MockMvc
* H2

### DevOps

* Docker
* Docker Compose
* GitHub Actions
* Terraform
* Kubernetes

### Observability

* Prometheus
* Grafana
* Loki

***

## Inter-Service Communication

Microservices communicate primarily in an **asynchronous** manner via Apache Kafka.

### Kafka Topics

```text
user-created
transaction-created
transaction-success
transaction-failed
```

Example of transaction processing:

```text
Client
  │
  ▼
Transaction Service
  │
  │ transaction-created
  ▼
Kafka
  │
  ▼
Wallet Service
  │
  ├── Debit sender wallet
  │
  └── Credit recipient wallet
  │
  ▼
transaction-success
  │
  ▼
Notification Service
```

***

## Security

Authentication is based on **JWT**.

The general flow is:

```text
Login
  │
  ▼
Auth Service
  │
  ▼
Access Token
  │
  ▼
Client
  │
  │ Authorization: Bearer <token>
  ▼
Microservices
```

Secrets and sensitive information must never be committed to Git.

Examples:

```text
JWT secrets
Database passwords
Kafka credentials
API keys
Cloud credentials
```

Instead, use environment variables or secrets managed by the deployment environment.

***

## Running with Docker

### Prerequisites

* Docker
* Docker Compose

### Start the Entire Project

From the root directory:

```bash
docker compose up --build
```

### Stop the Services

```bash
docker compose down
```

### Stop and Remove Volumes

```bash
docker compose down -v
```

***

## Testing

Each microservice has its own tests.

Example:

```bash
cd auth-service
./mvnw test
```

```bash
cd wallet-service
./mvnw test
```

```bash
cd transaction-service
./mvnw test
```

```bash
cd notification-service
./mvnw test
```

Or, from the root, tests for all services can be run via the CI pipeline.

***

## CI/CD

The project uses **GitHub Actions** to automate verification and deployment.

Planned pipeline:

```text
git push
   │
   ▼
GitHub Actions
   │
   ├── Tests
   ├── Build
   ├── Docker Build
   └── Docker Push
          │
          ▼
      Kubernetes
          │
          ▼
       Deploy
```

***

## Infrastructure

Infrastructure is managed with **Terraform**, and applications are deployed on **Kubernetes**.

```text
Terraform
    │
    ▼
Cloud Infrastructure
    │
    ▼
Kubernetes Cluster
    │
    ├── auth-service
    ├── wallet-service
    ├── transaction-service
    └── notification-service
```

Infrastructure files are available in:

```text
infrastructure/
├── terraform/
└── kubernetes/
```

***

## Observability

The production environment includes an observability stack based on:

```text
Applications
     │
     ├──────────────► Prometheus ──────► Grafana
     │                    metrics
     │
     └──────────────► Loki ────────────► Grafana
                          logs
```

This will enable monitoring of:

* CPU and memory
* Number of requests
* HTTP latency
* HTTP errors
* Kafka errors
* Application logs
* JVM metrics
* Microservice health

***

## Repository Structure

```text
wali/
│
├── auth-service/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
│
├── wallet-service/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
│
├── transaction-service/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
│
├── notification-service/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
│
├── infrastructure/
│   ├── terraform/
│   └── kubernetes/
│
├── .github/
│   └── workflows/
│       ├── ci.yml
│       └── cd.yml
│
├── docker-compose.yml
├── .gitignore
└── README.md
```

***

## Project Objectives

* Design a robust microservices architecture
* Implement asynchronous communication with Kafka
* Separate databases per service
* Secure APIs with JWT
* Containerize services with Docker
* Automate testing and deployments with GitHub Actions
* Provision infrastructure with Terraform
* Deploy microservices with Kubernetes
* Set up a complete observability solution with Grafana, Prometheus, and Loki

***

## Development

The project is organized as a **monorepo** to centralize the code for the different microservices, along with their infrastructure and DevOps configuration.

However, each service remains **independent in terms of its code, database, and deployment**.