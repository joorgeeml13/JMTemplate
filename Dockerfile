# Build stage
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Install Maven and build tools
RUN apk add --no-cache maven bash curl

# Copy Maven wrapper and source files
COPY mvnw mvnw.cmd ./
COPY .mvn ./.mvn
COPY pom.xml ./
COPY src ./src

# Build the application with Maven wrapper
RUN chmod +x ./mvnw && \
    ./mvnw clean package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy the built artifact from build stage
COPY --from=build /app/target/*.jar app.jar

# Create logs directory
RUN mkdir -p logs && chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose port from environment variable or default
EXPOSE ${SERVER_PORT:-9000}

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]