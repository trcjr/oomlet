# ========================
# Stage 1 - Dependencies
# ========================
FROM maven:3.9.6-eclipse-temurin-21 AS deps
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline -B

# ========================
# Stage 2 - Build
# ========================
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /build

# Copy dependencies from deps stage
COPY --from=deps /root/.m2 /root/.m2

# Copy source code
COPY pom.xml .
COPY src ./src

# Build with optimized settings
RUN mvn clean package \
    -Dmaven.test.parallel=true \
    -Dmaven.test.forkCount=2 \
    -Dmaven.test.reuseForks=true \
    -Dmaven.compiler.fork=true \
    -Dmaven.compiler.useIncrementalCompilation=true \
    -DskipTests=false \
    -B

# ========================
# Stage 3 - Runtime
# ========================
FROM eclipse-temurin:21-jre-jammy AS runtime

WORKDIR /app

# Only copy the built JAR
ARG JAR_FILE=oomlet-*.jar
COPY --from=builder /build/target/${JAR_FILE} app.jar

# Optional config
COPY src/main/resources/endpoint_health_indicator_config.yml /opt/

# Create non-root user
RUN groupadd -r -g 2000 application && useradd -m -d /home/application/ -s /bin/bash -u 2000 -r -g application application

EXPOSE 8080

USER application

ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
