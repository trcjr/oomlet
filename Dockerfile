# Use OpenJDK 24 slim base image
FROM openjdk:22-jdk-slim

# Create working directory inside container
WORKDIR /app

# Copy the built JAR file into the container
COPY target/oomlet-0.0.1.jar app.jar

# Expose port 8080 (default Spring Boot port)
EXPOSE 8080

# Allow dynamic JVM options via environment variable
ENV JAVA_OPTS=""

# Startup command
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]