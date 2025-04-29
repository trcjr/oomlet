# ========================
# Stage 1 - Build
# ========================
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /build

COPY pom.xml .
COPY src ./src

RUN mvn clean verify package

# ========================
# Stage 2 - Runtime
# ========================
FROM eclipse-temurin:21-jre-jammy




WORKDIR /app

# Only copy the built JAR
COPY --from=builder /build/target/oomlet-*.jar app.jar

# Optional config
COPY src/main/resources/endpoint_health_indicator_config.yml /opt/

# Create non-root user
RUN groupadd -r -g 2000 application && useradd -m -d /home/application/ -s /bin/bash -u 2000 -r -g application application

EXPOSE 8080

USER application

ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]