# Wali

**Wali** is a digital wallet platform built with a **microservices architecture**.

The project provides services for authentication, wallet management, transactions, and notifications, with a frontend application used to interact with the platform.

---

## Architecture

Wali is organized as a monorepo containing the backend microservices, frontend application, infrastructure, and CI/CD configuration.

```text
                           ┌──────────────┐
                           │   Frontend   │
                           └──────┬───────┘
                                  │
                                  ▼
                         ┌────────────────┐
                         │  API Gateway   │
                         └───────┬────────┘
                                 │
              ┌──────────────────┼──────────────────┐
              │                  │                  │
              ▼                  ▼                  ▼
       ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
       │Auth Service │    │Wallet       │    │Transaction  │
       │             │    │Service      │    │Service      │
       └─────────────┘    └─────────────┘    └─────────────┘
              │                  │                  │
              └──────────────────┼──────────────────┘
                                 │
                                 ▼
                            ┌──────────┐
                            │  Kafka   │
                            └────┬─────┘
                                 │
                                 ▼
                         ┌─────────────────┐
                         │ Notification    │
                         │ Service         │
                         └─────────────────┘
```

---

## Project Structure

```text
wali/
│
├── backend/
│   ├── api-gateway/
│   ├── auth-service/
│   ├── wallet-service/
│   ├── transaction-service/
│   └── notification-service/
│
├── frontend/
│
├── infrastructure/
│   ├── terraform/
│   └── kubernetes/
│
├── asset/
│
├── .github/
│   └── workflows/
│
├── .gitignore
├── qodana.yaml
└── README.md
```

---

## Backend

The backend is composed of several independent microservices:

| Service                | Description                        |
| ---------------------- | ---------------------------------- |
| `api-gateway`          | Entry point for client requests    |
| `auth-service`         | Authentication and user management |
| `wallet-service`       | Wallet and balance management      |
| `transaction-service`  | Transaction management             |
| `notification-service` | Notification management            |

Each microservice is designed to be independently developed, tested, built, and deployed.

---

## Frontend

The `frontend/` directory contains the client application used to interact with the Wali platform.

The frontend communicates with the backend through the **API Gateway**.

---

## Technologies

### Backend

* Java
* Spring Boot
* Spring Security
* JWT
* Spring Data JPA
* PostgreSQL
* Apache Kafka

### Frontend

* React / React Native

### DevOps

* Docker
* Docker Compose
* GitHub Actions
* Terraform
* Kubernetes

### Monitoring

* Prometheus
* Grafana
* Loki

---

## Getting Started

### Prerequisites

Make sure you have installed:

* Git
* Java 17
* Maven
* Node.js
* Docker
* Docker Compose

Depending on the part of the project you want to run, additional dependencies may be required.

---

## Clone the Repository

```bash
git clone <repository-url>
cd wali
```

---

## Start the Backend

Each backend service is an independent Spring Boot application.

For example:

```bash
cd backend/auth-service
./mvnw spring-boot:run
```

On Windows:

```powershell
cd backend/auth-service
.\mvnw.cmd spring-boot:run
```

The same approach can be used for the other services:

```text
backend/
├── api-gateway/
├── auth-service/
├── wallet-service/
├── transaction-service/
└── notification-service/
```

---

## Start the Frontend

Go to the frontend directory:

```bash
cd frontend
```

Install dependencies:

```bash
npm install
```

Then start the application using the command defined by the frontend project.

For example:

```bash
npm run dev
```

---

## Start with Docker

If a Docker Compose configuration is provided at the root of the project:

```bash
docker compose up --build
```

To stop the containers:

```bash
docker compose down
```

---

## Infrastructure

The `infrastructure/` directory contains the configuration used to provision and deploy the platform.

```text
infrastructure/
├── terraform/
└── kubernetes/
```

Terraform is used for infrastructure provisioning, while Kubernetes is used to manage application deployments.

---

## CI/CD

GitHub Actions is used to automate the development workflow.

The CI/CD pipelines can perform tasks such as:

```text
Push / Pull Request
        │
        ▼
   GitHub Actions
        │
        ├── Test
        ├── Build
        ├── Docker Image
        └── Deployment
```

Workflow files are located in:

```text
.github/workflows/
```

---

## Development

Wali follows a **monorepo** approach.

The repository contains:

* Backend microservices
* Frontend application
* Infrastructure
* CI/CD configuration

Although they are stored in the same repository, backend services remain independent and can be developed, tested, built, and deployed separately.

---

## License

This project is currently under development.
