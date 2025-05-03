# 🥚 OOMlet

![Build Status](https://github.com/trcjr/oomlet/actions/workflows/000-mono-workflow.yml/badge.svg)
![Develop Snapshot](https://github.com/trcjr/oomlet/actions/workflows/001-mono-develop.yml/badge.svg?branch=develop)
![Security Scan](https://github.com/trcjr/oomlet/actions/workflows/security-scan.yml/badge.svg)

[![Coverage](https://codecov.io/gh/trcjr/oomlet/branch/main/graph/badge.svg)](https://codecov.io/gh/trcjr/oomlet)
[![Develop Coverage](https://codecov.io/gh/trcjr/oomlet/branch/develop/graph/badge.svg)](https://codecov.io/gh/trcjr/oomlet/branch/develop)
![Tests](https://img.shields.io/badge/tests-100%25%20passing-brightgreen)

![Java](https://img.shields.io/badge/Java-21-blue)
![Last Commit](https://img.shields.io/github/last-commit/trcjr/oomlet)
![License](https://img.shields.io/badge/license-MIT-blue)

![Crashing With Style](https://img.shields.io/badge/crashing-with--style-yellow)

**OOMlet** — a lightweight, chaos-friendly QA and debugging Spring Boot application designed to help you **test**, **stress**, and **harden** your systems.

🍳 **Crack limits. Scramble resources. Cook up resilience.**

---

## 📚 Table of Contents
- [Quick Start](#-quick-start)
- [API Endpoints](#-api-endpoints)
- [Stress Testing Endpoints](#-stress-testing-endpoints)
- [Connectivity & Delay Simulation](#-connectivity--delay-simulation)
- [Crash the Application](#-crash-the-application)
- [Runtime Configuration](#-runtime-configuration)
- [Docker and Kubernetes Support](#-docker-and-kubernetes-support)
- [Testing and Code Coverage](#-testing-and-code-coverage)
- [Signal Handling](#-signal-handling)
- [Architecture Overview](#-architecture-overview)

## 🚀 Features

- ✅ Dynamic health check toggling (pass/fail)
- ✅ Simulate arbitrary HTTP response codes
- ✅ Memory and file handle stress testing endpoints
- ✅ CPU load simulation
- ✅ Graceful OS signal handling (SIGINT, SIGTERM, USR1, USR2)
- ✅ Runtime log level adjustment (dynamic)
- ✅ Crash with specific exit codes
- ✅ Outbound connectivity tester (ping a URL)
- ✅ Simulate response latency
- ✅ Full Actuator integration
- ✅ Code coverage enforcement (80% minimum)
- ✅ Built for local development, Docker, and Kubernetes

---

## 🛠 Quick Start

### 📋 Prerequisites

- Java 17+ (Temurin recommended)
- Maven 3.8+ (no need to install if using the Maven Wrapper)

### 1. Build the application

```bash
./mvnw clean package
```

### 2. Run the application

```bash
java -jar target/oomlet-0.0.8.jar
```

(Optional) Override port:

```bash
SERVER_PORT=9090 java -jar target/oomlet-0.0.8.jar
```

---

## 📖 API Endpoints

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
| `/api/crash?code=137` | POST | Exit the process with specific code |
| `/api/ping?url=https://example.com` | GET | Test outbound connectivity to a URL |
| `/api/latency?delayMillis=1500` | GET | Simulate response latency for timeout testing |

---

## 🧪 Stress Testing Endpoints

### Memory Allocation

```bash
curl 'http://localhost:8080/api/allocate-memory?bytes=104857600'
```

### File Handle Load

```bash
curl 'http://localhost:8080/api/open-files?count=100'
```

### CPU Burn

```bash
curl 'http://localhost:8080/api/burn-cpu?millis=5000&threads=4'
```

---

## 📡 Connectivity & Delay Simulation

### Outbound Ping

```bash
curl 'http://localhost:8080/api/ping?url=https://example.com'
```

### Simulate Latency

```bash
curl 'http://localhost:8080/api/latency?delayMillis=1500'
```

---

## 💥 Crash the Application

Use this to simulate process failure and container exit.

```bash
curl -X POST 'http://localhost:8080/api/crash?code=137'
```

---

## 🔧 Runtime Configuration

### Log Level Adjustment

```bash
curl -X POST 'http://localhost:8080/api/logging/spring?level=DEBUG'
```

```bash
curl 'http://localhost:8080/api/logging/spring'
```

### JVM Memory Options

You can limit or expand memory usage for stress tests:

```bash
JAVA_OPTS="-Xmx512m -Xms128m" java -jar target/oomlet-0.0.8.jar
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

Run unit and integration tests:

```bash
./mvnw clean verify
```

Open coverage report:

```bash
open target/site/jacoco/index.html
```

View live coverage:

- GitHub Pages: https://trcjr.github.io/oomlet  
- Codecov: https://codecov.io/gh/trcjr/oomlet

✅ Enforced 80%+ line coverage.  
✅ Build fails if coverage threshold not met.  
✅ CI/CD runs on each push via GitHub Actions.

### 🔒 Coverage Gate

This project enforces 80% minimum line coverage using JaCoCo.

Fail the build on low coverage by editing `pom.xml`:

```xml
<rule>
  <element>BUNDLE</element>
  <limits>
    <limit>
      <counter>INSTRUCTION</counter>
      <value>COVEREDRATIO</value>
      <minimum>0.80</minimum>
    </limit>
  </limits>
</rule>
```

Or use `.codecov.yml` in root:

```yaml
coverage:
  status:
    project:
      default:
        target: 80%
        threshold: 1%
```

### 📜 Workflow Rules

All GitHub Actions workflows in this project follow strict rules for consistency and reliability. This includes:

- ✅ Git short hashes must be set using `git rev-parse --short HEAD` and passed via `$GITHUB_OUTPUT`
- ✅ Helm chart versions must be valid [SemVer](https://semver.org) using the `-dev.<short_hash>` format
- ✅ CI/CD steps are explicitly structured to avoid flakiness and ensure reproducibility

---

## 🚦 Signal Handling

OOMlet handles:

- SIGINT (`Ctrl+C`)
- SIGTERM (`docker stop`, `kubectl delete pod`)
- SIGHUP, SIGQUIT, SIGUSR1, SIGUSR2

Gracefully shuts down or logs custom signals.

- SIGUSR1 — logs current heap usage and active memory state
- SIGUSR2 — logs thread dump and internal diagnostics

---

## 🗺 Architecture Overview

```plaintext
┌────────────────────────────┐
│        Client / User        │
└─────────────┬──────────────┘
              │
        HTTP Requests
              │
┌─────────────▼──────────────┐
│      Spring Boot Server     │
└─────────────┬──────────────┘
              │
    ┌─────────┼──────────┬──────────────┬──────────────┬─────────────┐
    │         │           │              │              │
┌───▼───┐ ┌────▼────┐ ┌────▼────┐ ┌───────▼─────┐ ┌──────▼────┐
│status│ │open-files││allocate │ │ /ulimits    │ │actuator   │
│      │ │          ││memory   │ │ burn-cpu    │ │ health, etc│
└──────┘ └──────────┘└─────────┘ └─────────────┘ └───────────┘
              │
              ▼
    ┌─────────────────────────────┐
    │ SignalHandlerService        │
    └─────────┬───────────────────┘
              ▼
        Graceful Shutdown
```

---

## 📄 License

MIT — see [LICENSE](./LICENSE)

---

## ✍️ Contributing

We welcome PRs and issues. Start with [CONTRIBUTING.md](./CONTRIBUTING.md)  
Look for issues labeled `good first issue` to help out!
