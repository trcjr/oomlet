# 🥚 OOMlet

![Build Status](https://github.com/trcjr/oomlet/actions/workflows/000-mono-workflow.yml/badge.svg)
[![Coverage](https://codecov.io/gh/trcjr/oomlet/branch/main/graph/badge.svg)](https://codecov.io/gh/trcjr/oomlet)
![License](https://img.shields.io/badge/license-MIT-blue)
![Crashing With Style](https://img.shields.io/badge/crashing-with--style-yellow)

**OOMlet** — a lightweight, chaos-friendly QA and debugging Spring Boot application designed to help you **test**, **stress**, and **harden** your systems.

🍳 **Crack limits. Scramble resources. Cook up resilience.**

---

## 🚀 Features

- ✅ Dynamic health check toggling (pass/fail)
- ✅ Simulate arbitrary HTTP response codes
- ✅ Memory and file handle stress testing endpoints
- ✅ CPU load simulation
- ✅ Graceful OS signal handling (SIGINT, SIGTERM, USR1, USR2)
- ✅ Runtime log level adjustment (dynamic)
- ✅ Full Actuator integration
- ✅ Code coverage enforcement (80% minimum)
- ✅ Built for local development, Docker, and Kubernetes

---

## 🛠 Quick Start

## 📋 Prerequisites

- Java 17+ (Temurin recommended)
- Maven 3.8+ (no need to install if using the Maven Wrapper)

### 1. Build the application

```bash
./mvnw clean package
```

(Uses the Maven Wrapper for portability.)

### 2. Run the application

```bash
java -jar target/oomlet-0.0.7.jar
```

(Optional) Override port:

```bash
SERVER_PORT=9090 java -jar target/oomlet-0.0.7.jar
```

### 3. Explore available endpoints

| Endpoint | Method | Purpose |
|:---------|:-------|:--------|
| `/actuator/health` | GET | Standard Spring Boot health check |
| `/api/health-toggle/enable` | POST | Set custom health indicator to **pass** |
| `/api/health-toggle/disable` | POST | Set custom health indicator to **fail** |
| `/api/status?responseCode=404` | GET | Simulate specific HTTP status responses |
| `/api/open-files?count=100` | GET | Stress test file descriptor limits |
| `/api/allocate-memory?bytes=104857600` | GET | Allocate memory blocks to stress JVM heap |
| `/api/burn-cpu?millis=1000&threads=2` | GET | Generate CPU load |
| `/api/ulimits` | GET | View OS resource limits (parsed `ulimit`) |
| `/api/logging/spring?level=DEBUG` | POST | Change Spring framework log level dynamically |

---

## 🧪 Stress Testing Endpoints

### Memory Allocation

**Endpoint:**  
`GET /api/allocate-memory?bytes=N`

- Attempts to allocate memory in 1MB blocks.
- Reports `requestedBytes`, `allocatedBytes`, and `failedBytes`.
- Example:
  ```bash
  curl 'http://localhost:8080/api/allocate-memory?bytes=104857600'
  ```

### File Handle Load

**Endpoint:**  
`GET /api/open-files?count=N`

- Opens `N` temporary files (read-only) and reports how many succeeded.
- Example:
  ```bash
  curl 'http://localhost:8080/api/open-files?count=100'
  ```

### CPU Burn

**Endpoint:**  
`GET /api/burn-cpu?millis=5000&threads=4`

- Consumes CPU cycles for a specified duration and number of threads.
- Example:
  ```bash
  curl 'http://localhost:8080/api/burn-cpu?millis=5000&threads=4'
  ```

---

## 📖 API - Other Useful Endpoints

### Health Check Toggle

```bash
curl -X POST 'http://localhost:8080/api/health-toggle/disable'
curl -X POST 'http://localhost:8080/api/health-toggle/enable'
```

Dynamically simulate health degradation and recovery.

---

### Runtime Log Level Adjustment

**Change Spring log level at runtime:**

```bash
curl -X POST 'http://localhost:8080/api/logging/spring?level=DEBUG'
```

**Get current Spring log level:**

```bash
curl 'http://localhost:8080/api/logging/spring'
```

---

## 📦 Docker and Kubernetes Support

### Build Docker Image

```bash
docker build -t oomlet:latest .
```

### Run Locally

```bash
docker run -p 8080:8080 oomlet:latest
```

✅ Designed to be liveness- and readiness-probe friendly.

✅ Docker image built for minimal size and startup speed.

---

## ⚙️ Configuration

| Variable | Purpose | Example |
|:---------|:--------|:--------|
| `SERVER_PORT` | Override server port | `SERVER_PORT=9090` |
| `JAVA_OPTS` | Pass JVM options | `JAVA_OPTS="-Xmx512m"` |

---

## 🧪 Testing and Code Coverage

Run unit and integration tests with coverage:

```bash
./mvnw clean verify
```

View code coverage report locally:

```bash
open target/site/jacoco/index.html
```

Or see it live at:

➡️ https://trcjr.github.io/oomlet

Also published to Codecov:

➡️ https://codecov.io/gh/trcjr/oomlet

✅ Enforced 80%+ line coverage.

✅ Build will fail if coverage threshold is not met.

✅ CI/CD with GitHub Actions ensures test + coverage thresholds are met on every push.

---

## 🚦 Graceful Shutdown and Signal Handling

OOMlet listens for:

- **SIGINT** (`Ctrl+C`)
- **SIGTERM** (`docker stop`, `kubectl delete pod`)
- **SIGHUP**, **SIGQUIT**, **SIGUSR1**, **SIGUSR2**

✅ Critical signals trigger a graceful Spring Boot shutdown.  
✅ Custom signals (USR1, USR2) are logged for future hooks.

---

## 🗺 Architecture Overview

```plaintext
┌────────────────────────────┐
│        Client / User        │
└─────────────┬───────────────┘
              │
        HTTP Requests
              │
┌─────────────▼───────────────┐
│      Spring Boot Server     │
│    (Embedded Web Server)    │
└─────────────┬───────────────┘
              │
    ┌─────────┼──────────┬───────────────┬──────────────┬─────────────┐
    │         │           │               │              │
┌───▼───┐ ┌────▼────┐ ┌────▼────┐ ┌────────▼──────┐ ┌─────▼─────┐
│ /api/ │ │ /api/   │ │ /api/   │ │ /api/ulimits  │ │ /actuator/│
│status │ │open-files││allocate │ │ /api/burn-cpu │ │health, etc│
└───────┘ └──────────┘└─────────┘└───────────────┘ └───────────┘
              │
              ▼
    ┌───────────────────────┐
    │ SignalHandlerService  │
    └─────────┬──────────────┘
              ▼
       Graceful Shutdown
```

---

## 📄 License

Released under the [MIT License](./LICENSE)  
See [CONTRIBUTING.md](./CONTRIBUTING.md) for guidelines.

---

## ✍️ Contributing

We welcome issues, suggestions, and pull requests!

Please see [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

New here? Start with an issue labeled "good first issue".

---

```plaintext
    🥚
    / \
   / _ \
  | (_) |
   \___/
  OOMlet: Cracking limits, cooking crashes 🍳
```
