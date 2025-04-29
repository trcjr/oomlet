# OOMlet

![Build Status](https://github.com/trcjr/oomlet/actions/workflows/build.yml/badge.svg)
[![Coverage Status](https://coveralls.io/repos/github/trcjr/oomlet/badge.svg?branch=main)](https://coveralls.io/github/trcjr/oomlet?branch=main)
![License](https://img.shields.io/badge/license-MIT-blue)
![Crashing With Style](https://img.shields.io/badge/crashing-with--style-yellow)

✅ A lightweight QA and debugging Spring Boot application designed to help you test, monitor, and simulate application health and response behavior.

## 🥚 About OOMlet

When you need to scramble your systems, crash your apps gently, or just cook up some chaos in your infrastructure —  
**OOMlet** is here to crack a few eggs so you can build stronger, more resilient software.

🍳 **Crack limits. Scramble resources. Debug better.**

---

## 🚀 Features

- Dynamic health check toggling (pass/fail)
- Simulated HTTP response codes via API
- Full Spring Boot Actuator endpoints
- Graceful handling of OS signals (SIGINT, SIGTERM)
- Code coverage enforcement (80% minimum)
- Built for local, Docker, and Kubernetes environments

## 🛠 Quick Start

1. **Build the application:**

```bash
mvn clean package
```

2. **Run the application:**

```bash
java -jar target/oomlet-0.0.1-SNAPSHOT.jar
```

3. **Use the available endpoints:**

| Endpoint | Method | Purpose |
|:---------|:-------|:--------|
| `/actuator/health` | GET | Standard Spring Boot health check |
| `/api/health-toggle/enable` | POST | Set health indicator to **pass** |
| `/api/health-toggle/disable` | POST | Set health indicator to **fail** |
| `/api/status?responseCode=404` | GET | Simulate arbitrary HTTP status codes |
| `/api/open-files?count=100` | GET | Attempt to open 100 file handles (temporary files) and report how many succeeded |
| `/api/allocate-memory?bytes=104857600` | GET | Attempt to allocate 100 MB of memory and report how much was successfully allocated |

---

## 🧪 Memory Allocation Stress Test Endpoint

**Endpoint:**  
`GET /api/allocate-memory?bytes=N`

**Parameters:**

- `bytes` — (required) Number of bytes to attempt to allocate.

**Behavior:**

- Attempts to allocate memory in 1 MB blocks until the requested number of bytes is allocated or an `OutOfMemoryError` occurs.
- Cleans up and releases all allocated memory after the operation.
- Returns a JSON report with the number of bytes requested, successfully allocated, and failed.

**Example request:**

```bash
curl 'http://localhost:8080/api/allocate-memory?bytes=104857600'
```

**Example JSON response:**

```json
{
  "requestedBytes": 104857600,
  "allocatedBytes": 104857600,
  "failedBytes": 0
}
```

**Use cases:**

- Explore JVM memory limits (e.g., heap size, memory limits in containers).
- Stress-test system memory management without crashing the server.
- Observe behavior near OutOfMemoryError thresholds.

✅ All allocated memory is released immediately after handling the request.
✅ Supports safe memory limit exploration.

---

## 🧪 File Handle Stress Test Endpoint

**Endpoint:**  
`GET /api/open-files?count=N`

**Parameters:**

- `count` — (required) Number of file handles to attempt to open.

**Behavior:**

- Attempts to open `N` temporary files in read-only mode.
- Closes all opened file handles before responding.
- Returns a JSON report with the number of handles requested, successfully opened, and failed.

**Example request:**

```bash
curl 'http://localhost:8080/api/open-files?count=50'
```

**Example JSON response:**

```json
{
  "requested": 50,
  "successfullyOpened": 50,
  "failed": 0
}
```

**Use cases:**

- Explore system limits (e.g., ulimit settings).
- Stress-test file descriptor usage without system crash.
- Safe automatic cleanup after each request.

✅ All file handles are properly closed after request handling.
✅ No open handle leaks.

---

## 🧪 Ulimit Information Endpoint

**Endpoint:**  
`GET /api/ulimits`

**Behavior:**

- Executes `ulimit -a` on the underlying operating system.
- Parses and returns system resource limits as JSON key-value pairs.

**Example request:**

```bash
curl 'http://localhost:8080/api/ulimits'
```

**Example JSON response:**

```json
{
  "core file size": "0",
  "data seg size": "unlimited",
  "file size": "unlimited",
  "max locked memory": "unlimited",
  "max memory size": "unlimited",
  "open files": "10240",
  "pipe size": "1",
  "stack size": "8176",
  "cpu time": "unlimited",
  "max user processes": "1333",
  "virtual memory": "unlimited"
}
```

**Use cases:**

- Explore system-imposed resource limits (e.g., open files, stack size).
- Debug and verify container or VM restrictions.

✅ Provides clear insights into runtime system limits.
✅ Useful for debugging Docker, Kubernetes, and bare metal environments.

---

## 🚦 Graceful Shutdown and Signal Handling

oomlet includes built-in OS signal handling to ensure **graceful shutdown** and **extensible behavior** in environments like **Docker** and **Kubernetes**.

**Supported signals:**

- **SIGINT** (`Ctrl+C` from terminal): Initiates graceful shutdown
- **SIGTERM** (sent by `docker stop`, `kubectl delete pod`, etc.): Initiates graceful shutdown
- **SIGHUP** (terminal hangup, reload trigger): Logged, no-op (optional config reload in future)
- **SIGQUIT** (Ctrl+\ forced quit): Initiates graceful shutdown
- **SIGUSR1** (user-defined action 1): Logged, placeholder for custom actions
- **SIGUSR2** (user-defined action 2): Logged, placeholder for custom actions

### How It Works

- Upon startup, oomlet registers handlers for `INT`, `TERM`, `HUP`, `QUIT`, `USR1`, and `USR2` signals.
- Signals are logged with detailed information.
- Critical shutdown signals initiate a clean termination through Spring lifecycle hooks.
- User-defined signals are reserved for future expansions (e.g., live health toggle or reloads).

✅ No abrupt shutdowns  
✅ No resource leaks  
✅ Clean exit in local, containerized, and cloud environments

📄 Related class: [`SignalHandlerService`](src/main/java/com/github/trcjr/oomlet/SignalHandlerService.java)

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
    ┌─────────┼──────────┬─────────────────────────┐
    │                    │                         │
┌───▼───┐         ┌──────▼──────┐  ┌───────────────▼──────────────┐
│ /api/ │         │ /actuator/  │  │ /api/open-files              │
│status │         │health toggle│  │(file handle stress testing)  │
└───┬───┘         └──────┬──────┘  └──────────────────────────────┘
    │                    │
    │                    │
    ▼                    ▼
Custom HTTP      Custom Health Indicator
Responses         (pass/fail toggleable)

              │
              ▼
    ┌───────────────────────┐
    │ SignalHandlerService   │
    │ (SIGINT / SIGTERM, etc.)│
    └─────────┬──────────────┘
              ▼
       Graceful Shutdown
```

✅ Lightweight  
✅ Easy to understand  
✅ GitHub markdown compatible  
✅ No external images needed

---

## 🧪 Testing and Coverage

- Run full build and tests:

```bash
mvn clean verify
```

- View coverage report:

```bash
open target/site/jacoco/index.html
```

✅ Build will fail if test coverage drops below **80%**.

---

## 🐳 Docker Support

Build Docker image:

```bash
docker build -t oomlet:latest .
```

Run locally:

```bash
docker run -p 8080:8080 oomlet:latest
```

✅ Ready for Kubernetes deployments.

---

## 📄 License

Licensed under the [MIT License](LICENSE).

---

## ✍️ Contributing

Contributions are welcome! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

---

```plaintext
    🥚
    / \
   / _ \
  | (_) |
   \___/
  OOMlet: Cracking limits, cooking crashes 🍳
```

---
