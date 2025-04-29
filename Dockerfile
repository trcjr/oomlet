# Use OpenJDK 24 slim base image
FROM openjdk:22-jdk-slim

# Create working directory inside container
WORKDIR /app

# Copy the built JAR file into the container
COPY target/oomlet-*.jar app.jar

# Add a non root user
RUN groupadd -r -g 2000 application && useradd -m -d /home/application/ -s /bin/bash -u 2000 -r -g application application

# Expose port 8080 (default Spring Boot port)
EXPOSE 8080

# Allow dynamic JVM options via environment variable
ENV JAVA_OPTS=""

USER application
# Startup command
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]