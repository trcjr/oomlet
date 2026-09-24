# ========================
# Stage 1 - Build (Maven + Java 25 LTS)
# ========================
FROM maven:3-eclipse-temurin-25 AS builder

WORKDIR /build

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build package
COPY src ./src
RUN mvn package -DskipTests

# ========================
# Stage 2 - Runtime (Java 25 JRE)
# ========================
FROM eclipse-temurin:25-jre

WORKDIR /app

# Create non-root application user
RUN groupadd -r -g 2000 application && \
    useradd -m -d /home/application/ -s /bin/bash -u 2000 -r -g application application

# Copy artifact with correct file ownership
COPY --chown=application:application --from=builder /build/target/oomlet-*.jar /app/app.jar
COPY --chown=application:application src/main/resources/endpoint_health_indicator_config.yml /opt/

EXPOSE 8080

USER application

ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
